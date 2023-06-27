package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import nz.cri.gns.newsite.exception.ResourceMissingException;
import nz.cri.gns.newsite.model.Island;
import nz.cri.gns.newsite.model.Site;
import nz.cri.gns.newsite.model.Site.SiteMode;
import nz.cri.gns.newsite.model.SiteDetailed;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteProximity;
import nz.cri.gns.newsite.repository.NewSiteIdRepository;
import nz.cri.gns.newsite.repository.NewSiteRepository;
import nz.cri.gns.newsite.utils.CoordinateConverter;
import nz.cri.gns.newsite.utils.NZMS260;
import nz.cri.gns.newsite.utils.QMAPSheet;
import nz.cri.gns.newsite.utils.Topo50;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

@Service
@Scope("singleton")
public class NewSiteServiceImpl implements NewSiteService {

    private static final Logger logger = LoggerFactory.getLogger(NewSiteServiceImpl.class);
    private List<SiteModel> _sites = new ArrayList<SiteModel>();

    @Autowired
    NewSiteRepository newSiteRepository;
    
    @Autowired
    NewSiteIdRepository newSiteIdRepository;
    
    @Autowired
    IslandService islandService;

    @Override
    public SiteModel insert(SiteModel s) {
        SiteModel newSite = newSiteRepository.save(s);
        logger.info("NewSite = " + newSite.toString());
        return newSite;
    }

    @Override
    public SiteModel update(SiteModel s) {
        retrieveWithNullCheck(s.getSiteId());
        logger.info("Update** = " + s.toString());
        return newSiteRepository.save(s);
    }

    private SiteModel retrieveWithNullCheck(int i) {
        SiteModel site = newSiteRepository.findById(i)
                .orElseThrow(
                        //                                                                logger.error("Site with id " + i + " not found");
                        () -> new ResourceMissingException("Site with id " + i + " not found"));
        logger.info(site.toString());
        return site;
    }

    @Override
    public void delete(int id) {
        SiteModel site = retrieveWithNullCheck(id);
        if (site.getComment().contains("test_data")) {
            site.setFlag(2);
        } else {
            site.setFlag(1);
        }
//		newSiteRepository.deleteById(id);
        newSiteRepository.save(site);
    }

    @Override
    public SiteModel find(int id) {
        return retrieveWithNullCheck(id);
    }

    @Override
//        @Query(value = "select * from sc.site_proposed where sc.site_proposed.orig_system_id = 16 limit 20")
    public List<SiteModel> findByOrigSystemId(int oid) {
        List<SiteModel> sites = new ArrayList<>();
        newSiteRepository.findAllByOrigSystemId(oid).forEach(sites::add);
        return sites;
    }

    @Override
    public List<SiteModel> listAllSites() {
        return newSiteRepository.findAll();
    }

    @Override
    public List<Site> findWithinBounds(Geometry bounds, SiteMode mode) {
        switch(mode)    {
            case COMPLETE:
                return newSiteRepository.findWithinBounds(bounds);
            default:
                return newSiteIdRepository.findWithinBounds(bounds);
        } 
    }

    @Override
    public List<SiteProximity> findCloseTo(Geometry Point, double distance) {
        try {
            List<SiteModel> sites = newSiteRepository.findCloseTo(Point, distance);
            List<SiteProximity> proximities = new ArrayList<>();
            CoordinateReferenceSystem crs;
            crs = CRS.decode("EPSG:4326", true);
            GeodeticCalculator gc = new GeodeticCalculator(crs);
            Coordinate p1 = Point.getCoordinate();
            gc.setStartingGeographicPoint(p1.x, p1.y);
            for (SiteModel s:sites) {
                Coordinate c = s.getShape().getCoordinate();
                gc.setDestinationGeographicPoint(c.getX(), c.getY());
                double proximity = gc.getOrthodromicDistance();
                SiteProximity sp = new SiteProximity(s,proximity);
                proximities.add(sp);
            }
            Collections.sort(proximities);
            return proximities;
        } catch (FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    /**
     * Returns all sites within the NZTOPO50 mapsheets provided (union of mapsheets, logical OR)
     * @param sheetNames the names of the mapsheets
     * @return all sites within the mapsheets, or an empty mapsheet if no matches exist
     */
    @Override
    public List<Site> findWithinTopo50Sheets(List<String> sheetNames, SiteMode mode) {
        
        Topo50 topo50 = Topo50.getInstance();
        if(sheetNames == null || sheetNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(topo50.getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        topo50.getDefaultEPSG()));
        
        try {
            switch(mode)    {
                case COMPLETE:
                    return newSiteRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, topo50.getDefaultEPSG()));
                default:
                    return newSiteIdRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, topo50.getDefaultEPSG()));
            } 
        } catch (TransformException | FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    
    /**
     * Returns all sites within the QMAP mapsheets provided (union of mapsheets, logical OR)
     * @param sheetNames the names of the mapsheets
     * @param mode
     * @return all sites within the mapsheets, or an empty mapsheet if no matches exist
     */
    @Override
    public List<Site> findWithinQMAPSheets(List<String> sheetNames, SiteMode mode) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(QMAPSheet.getInstance().getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        QMAPSheet.getDefaultEPSG()));
        
        try {
            switch(mode)    {
                case COMPLETE:
                    return newSiteRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, QMAPSheet.getDefaultEPSG()));
                default:
                    return newSiteIdRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, QMAPSheet.getDefaultEPSG()));
            } 
        } catch (TransformException | FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    /**
     * Returns all sites within the NZMG/NZMS260 mapsheets provided (union of mapsheets, logical OR)
     * @param sheetNames the names of the mapsheets
     * @param mode
     * @return all sites within the mapsheets, or an empty mapsheet if no matches exist
     */
    @Override
    public List<Site> findWithinNZMGSheets(List<String> sheetNames, SiteMode mode) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(NZMS260.getInstance().getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        NZMS260.getDefaultEPSG()));
        
        try {
            switch(mode)    {
                case COMPLETE:
                    return newSiteRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, NZMS260.getDefaultEPSG()));
                default:
                    return newSiteIdRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, NZMS260.getDefaultEPSG()));
            } 
        } catch (TransformException | FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    /**
     * Returns all sites within the Countries provided (logical OR)
     * @param countryNames the names of the countries
     * @param mode
     * @return all sites within the countries, or an empty list if no matches exist
     */
    @Override
    public List<Site> findWithinCountries(List<String> countryNames, SiteMode mode) {
        if(countryNames == null || countryNames.isEmpty())  {
            return new ArrayList<>();
        }
        
        switch(mode)    {
            case COMPLETE:
                return newSiteRepository.findByCountryCodes(countryNames);
            default:
                return newSiteIdRepository.findByCountryCodes(countryNames);
        } 
    }
    
    /**
     * Returns all sites within the Islands provided (union of islands, logical OR)
     * @param islandNames the names of the islands
     * @param mode
     * @return all sites within the islands, or an empty list if no matches exist
     */
    @Override
    public List<Site> findWithinIslands(List<String> islandNames, SiteMode mode) {
        if(islandNames == null || islandNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        List<Island> islands;
        for(String islandName: islandNames)   {
            islands = islandService.findByName(islandName);
            if(islands != null && islands.size() > 0)    {
                geometries.add(islands.get(0).getBBox());
            }
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        Island.getDefaultEPSG()));
        switch(mode)    {
            case COMPLETE:
                return newSiteRepository.findWithinBounds(unionOfMapsheets);
            default:
                return newSiteIdRepository.findWithinBounds(unionOfMapsheets);
        } 
    }
        
    
    /**
     * Filters by different map sheets, logical AND (intersection) between map sheet types
     * @param topo50Sheets
     * @param qmapSheets
     * @param nzmgSheets
     * @param countries
     * @param islands
     * @return 
     */
    @Override
    public List<Site> findWithinMapSheets(List<String> topo50Sheets, List<String> qmapSheets, List<String> nzmgSheets, List<String> countries, List<String> islands, SiteMode mode) {
        if((topo50Sheets == null || topo50Sheets.isEmpty())
        && (qmapSheets == null || qmapSheets.isEmpty())
        && (nzmgSheets == null || nzmgSheets.isEmpty())
        && (countries == null || countries.isEmpty())
        && (islands == null || islands.isEmpty())){
            return null;
        }
        List<Site> topo50Matches = findWithinTopo50Sheets(topo50Sheets, mode);
        List<Site> qmapMatches = findWithinQMAPSheets(qmapSheets, mode);
        List<Site> nzmgMatches = findWithinNZMGSheets(nzmgSheets, mode);
        List<Site> countryMatches = findWithinCountries(countries, mode);
        List<Site> islandMatches = findWithinIslands(islands, mode);
        
        List<Site> mixedAndMatched = new ArrayList<>();
        
        if(!topo50Matches.isEmpty())    {
            mixedAndMatched = topo50Matches.stream()
                    .distinct()
                    .collect(Collectors.toList());
        } 
        
        if(!qmapMatches.isEmpty())    {
            if(mixedAndMatched.isEmpty())   {   //no match yet
                mixedAndMatched = qmapMatches.stream()
                    .distinct()
                    .collect(Collectors.toList());
            } else {
                mixedAndMatched = mixedAndMatched.stream()
                    .distinct()
                    .filter(qmapMatches::contains)
                    .collect(Collectors.toList());
            } 
        }
        
        if(!nzmgMatches.isEmpty())    {
            if(mixedAndMatched.isEmpty())   {   //no match yet
                mixedAndMatched = nzmgMatches.stream()
                    .distinct()
                    .collect(Collectors.toList());
            } else {
                mixedAndMatched = mixedAndMatched.stream()
                    .distinct()
                    .filter(nzmgMatches::contains)
                    .collect(Collectors.toList());
            }    
        }
        
        if(!countryMatches.isEmpty())    {
            if(mixedAndMatched.isEmpty())   {   //no match yet
                mixedAndMatched = countryMatches.stream()
                    .distinct()
                    .collect(Collectors.toList());
            } else {
                mixedAndMatched = mixedAndMatched.stream()
                    .distinct()
                    .filter(countryMatches::contains)
                    .collect(Collectors.toList());
            }   
        }
        
        if(!islandMatches.isEmpty())    {
            if(mixedAndMatched.isEmpty())   {   //no match yet
                mixedAndMatched = islandMatches.stream()
                    .distinct()
                    .collect(Collectors.toList());
            } else {
                mixedAndMatched = mixedAndMatched.stream()
                    .distinct()
                    .filter(islandMatches::contains)
                    .collect(Collectors.toList());
            }   
        }
           
        return mixedAndMatched;        
    }
    
        /**
     * Convenience method to return (sort of) a GeoJSON representation of the union of bboxes
     * as defined by the list of mapsheets. Return type is a
     * @param sheetNames the names of the mapsheets
     * @return  multipolygon geometry without any attributes.
     */
    @Override
    public String getQMAPSheetsGeoJson(List<String> sheetNames) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return null;
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(QMAPSheet.getInstance().getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return null;
        
        return new GeoJsonWriter().write(
                UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        QMAPSheet.getDefaultEPSG()))
        );
    }
    
    @Override
    public SiteDetailed findDetails(int id) {
        SiteModel sm = retrieveWithNullCheck(id);
        
        if(sm == null)  {
            return null;
        }
        SiteDetailed sd = new SiteDetailed(sm);
        
        List<Island> islandsAtLocation = islandService.findByLocation((Point)sm.getShape());
        sd.setIsland(islandsAtLocation.isEmpty()? null : islandsAtLocation.get(0));
        
        return sd;
    }

}
