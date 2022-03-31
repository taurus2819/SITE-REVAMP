package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import nz.cri.gns.newsite.exception.ResourceMissingException;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteProximity;
import nz.cri.gns.newsite.repository.NewSiteRepository;
import nz.cri.gns.newsite.utils.CoordinateConverter;
import nz.cri.gns.newsite.utils.QMAPSheet;
import static nz.cri.gns.newsite.utils.QMAPSheet.getDefaultEPSG;
import nz.cri.gns.newsite.utils.Topo50;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
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
    public List<SiteModel> findWithinBounds(Geometry bounds) {
        return newSiteRepository.findWithinBounds(bounds);
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
    public List<SiteModel> findWithinTopo50Sheets(List<String> sheetNames) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(Topo50.getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        getDefaultEPSG()));
        
        try {
            return newSiteRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, Topo50.getDefaultEPSG()));
        } catch (TransformException | FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    
    /**
     * Returns all sites within the QMAP mapsheets provided (union of mapsheets, logical OR)
     * @param sheetNames the names of the mapsheets
     * @return all sites within the mapsheets, or an empty mapsheet if no matches exist
     */
    @Override
    public List<SiteModel> findWithinQMAPSheets(List<String> sheetNames) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return new ArrayList<>();
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(QMAPSheet.getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return new ArrayList<>();
        
        Geometry unionOfMapsheets =  UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        getDefaultEPSG()));
        
        try {
            return newSiteRepository.findWithinBounds(CoordinateConverter.convertGeometryCoordinates(unionOfMapsheets, QMAPSheet.getDefaultEPSG()));
        } catch (TransformException | FactoryException ex) {
            java.util.logging.Logger.getLogger(NewSiteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    /**
     * Convenience method to return (sort of) a GeoJSON represnetation of the union of bboxes
     * as defined by the list of mapsheets. Return type is a
     * @param sheetNames the names of the mapsheets
     * @return  multipolygon gemeotry without any attributes.
     */
    @Override
    public String getQMAPSheetsGeoJson(List<String> sheetNames) {
        
        if(sheetNames == null || sheetNames.isEmpty())  {
            return null;
        }
        List<Geometry> geometries = new ArrayList<>();
        for(String sheetName: sheetNames)   {
            geometries.add(QMAPSheet.getBoundingBox(sheetName));
        }
        if(geometries.isEmpty())
            return null;
        
        return new GeoJsonWriter().write(
                UnaryUnionOp.union(new GeometryCollection(
                        geometries.toArray(new Geometry[0]),
                        new PrecisionModel(PrecisionModel.FLOATING), 
                        getDefaultEPSG()))
        );
    }

}
