package nz.cri.gns.newsite.controller;

/**
 *
 * @author sitikond
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import nz.cri.gns.newsite.model.Island;
import nz.cri.gns.newsite.model.MapSheetPayload;
import nz.cri.gns.newsite.model.SiteDetailed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteModelInput;
import nz.cri.gns.newsite.model.SiteProximity;
import nz.cri.gns.newsite.service.AuditLogService;
import nz.cri.gns.newsite.service.IslandService;
import nz.cri.gns.newsite.service.MasterFileService;
import nz.cri.gns.newsite.service.NewSiteService;
import nz.cri.gns.newsite.service.QMAPService;
import nz.cri.gns.newsite.utils.CoordinateConverter;
import nz.cri.gns.newsite.utils.OrigCoord;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Point;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins="*", allowedHeaders="*")
public class NewSiteController {

    @Autowired
    NewSiteService newSiteService;

    @Autowired
    AuditLogService auditLogService;
    
    @Autowired
    IslandService islandService;
    
    @Autowired
    QMAPService qmapService;
    
    @Autowired
    MasterFileService masterFileService;

    @RequestMapping("/sites/origsysid/{oid}")
    public List<SiteModel> getAllSites(@PathVariable int oid) {
        return newSiteService.findByOrigSystemId(oid);
    }


    @RequestMapping("/sites/{id}")
    public SiteModel getSite(@PathVariable int id) {
        return newSiteService.find(id);
    }

    @RequestMapping("/sites/{id}/details")
    public SiteDetailed getSiteDetails(@PathVariable int id) {
        return newSiteService.findDetails(id);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/site")
    public SiteModel addSite(@RequestBody SiteModelInput siteInput, HttpServletResponse response) throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        SiteModel site = siteInput.toSiteModel();
        site = newSiteService.insert(site);
        int newlyCreatedSiteId = site.getSiteId();
        List<String> siteinfoBefore = new ArrayList<>();
        siteinfoBefore.add("message: Newly Created");
        AuditLog newAuditLog = auditLogCreator(site, newlyCreatedSiteId, siteinfoBefore);
        site.addAuditLog(newAuditLog);
        auditLogService.insert(newAuditLog);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return site;
    }

    private AuditLog auditLogCreator(SiteModel site, int createdOrModifiedSiteId, List<String> siteinfoBefore) throws JsonProcessingException, JSONException {
        JSONObject logTimestampMsg;
        logTimestampMsg = new JSONObject();
        logTimestampMsg.put("timestamp", new Date());
        logTimestampMsg.put("info", site.getAuditMsg());
        logTimestampMsg.put("ownerId", site.getOwnerId());
        logTimestampMsg.put("before", siteinfoBefore);
        ObjectMapper mapper = new ObjectMapper();
        AuditLog newAuditLog = new AuditLog(createdOrModifiedSiteId, mapper.readTree(logTimestampMsg.toString())); //, site);
        return newAuditLog;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "/site/{id}")
    public SiteModel updateSite(@RequestBody SiteModelInput site, @PathVariable int id) throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        final SiteModel siteById = newSiteService.find(id);        //find(id) will do a sql select to physically fetch the entity from db, which is not required when just updating
        //so, use getOne(id) which gets a referenceobject and does not fetch it from the db.
        List<String> siteinfoBefore = new ArrayList<>();
        siteinfoBefore.add("siteName:" + siteById.getSiteName());
        siteinfoBefore.add("lat:" + siteById.getLat());
        siteinfoBefore.add("lon:" + siteById.getLon());
        siteinfoBefore.add("origCoord:" + siteById.getOrigCoord());
        SiteModel modifiedSite = site.toSiteModel();
        siteById.setSiteName(modifiedSite.getSiteName());
        siteById.setLat(modifiedSite.getLat());
        siteById.setLon(modifiedSite.getLon());
        siteById.setMethodId(modifiedSite.getMethodId());
        siteById.setAccuracy(modifiedSite.getAccuracy());
        siteById.setDirections(modifiedSite.getDirections());
        siteById.setOrigSystemId(modifiedSite.getOrigSystemId());
        siteById.setOrigCoord(modifiedSite.getOrigCoord());
        siteById.setHeight(modifiedSite.getHeight());
        siteById.setHeightMethodId(modifiedSite.getHeightMethodId());
        siteById.setHeightAccuracy(modifiedSite.getHeightAccuracy());
        siteById.setCountryCode(modifiedSite.getCountryCode());
        siteById.setFlag(modifiedSite.getFlag());
        siteById.setComment(modifiedSite.getComment());
        siteById.setOwnerId(modifiedSite.getOwnerId());
        siteById.setAuditMsg(modifiedSite.getAuditMsg());
        AuditLog newAuditLog = auditLogCreator(siteById, id, siteinfoBefore);
        siteById.addAuditLog(newAuditLog);
        auditLogService.insert(newAuditLog);
        newSiteService.update(siteById);
        return siteById;
    }

    @RequestMapping(value = "/sites/format/{id}", method = {RequestMethod.GET}, produces = "application/json")
    public String epsgInfo(@PathVariable int id) {
        SiteModel site = getSite(id);
        System.out.println("Site in JSON = " + site);
        return OrigCoord.getEpsgInfoJsonString(site.getOrigSystemId(), site.getOrigCoord());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/site/{id}")
    public void delete(@PathVariable Integer id, HttpServletResponse response) {
        newSiteService.delete(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    @RequestMapping(
            value = "/site/{id}/masterfile", 
            method = {RequestMethod.GET})
    public int getMasterFile(
            @PathVariable int id, 
            @RequestParam(value = "registrationAreaId") int registrationAreaId,
            @RequestParam(value = "backlogFeature", defaultValue = "false") boolean isBacklogFeature) {
        SiteModel site = getSite(id);
        return masterFileService.getMasterFile(site, registrationAreaId, isBacklogFeature);
    }

/**
 * Query the Site database for all points within a given rectangle
 * 
 * @param minNorth 
 * @param minEast
 * @param maxNorth
 * @param maxEast
 * @param epsg
 * @param response
 * @return
 * @throws InvalidLatLonFormat
 * @throws InvalidOrigCoordinate
 * @throws FactoryException
 * @throws MismatchedDimensionException
 * @throws TransformException
 * @throws JsonProcessingException 
 */
    @RequestMapping("/sites/query/bbox")
    public List<SiteModel> getAllSitesWithin(@RequestParam(value = "minNorth") double minNorth, @RequestParam(value = "minEast") double minEast,
            @RequestParam(value = "maxNorth") double maxNorth, @RequestParam(value = "maxEast") double maxEast, @RequestParam(value = "EPSG") int epsg, HttpServletResponse response)
            throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {

        return newSiteService.findWithinBounds(CoordinateConverter.convertBbox(minNorth, minEast, maxNorth, maxEast, epsg));
    }
    
    @RequestMapping("/sites/query/mapsheets")
    List<SiteModel> getAllSitesWithinMapSheets(
            @RequestParam(value = "topo50sheet", required=false) String[] topo50SheetNames, 
            @RequestParam(value = "qmapSheet", required=false) String[] qmapSheetNames,
            @RequestParam(value = "nzmgSheet", required=false) String[] nzmgSheetNames,
            @RequestParam(value = "island", required=false) String[] islandNames)   {
        
        long start = System.nanoTime();
        List<SiteModel> result = newSiteService.findWithinMapSheets(
                topo50SheetNames != null ? Arrays.asList(topo50SheetNames) : new ArrayList<>(), 
                qmapSheetNames != null ? Arrays.asList(qmapSheetNames) : new ArrayList<>(),
                islandNames != null ? Arrays.asList(islandNames) : new ArrayList<>());
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.err.println(String.format("API response time: %d ms", timeElapsed/1000000));
        
        return result;
    }
    
    @RequestMapping("/sites/query/mapsheets/qmap")
    List<SiteModel> getAllSitesWithinQMapSheets( 
            @RequestParam(value = "qmapSheet") String[] qmapSheetNames)   {
       
        long start = System.nanoTime();
        List<SiteModel> result = newSiteService.findWithinQMAPSheets(Arrays.asList(qmapSheetNames));
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.err.println(String.format("API response time: %d ms", timeElapsed/1000000));
        
        return result;
    }
    
    @RequestMapping("/sites/query/mapsheets/topo50")
    List<SiteModel> getAllSitesWithiTopo50Sheets(
            @RequestParam(value = "topo50sheet") String[] topo50SheetNames)   {
        
        long start = System.nanoTime();
        List<SiteModel> result = newSiteService.findWithinTopo50Sheets(Arrays.asList(topo50SheetNames));
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.err.println(String.format("API response time: %d ms", timeElapsed/1000000));
        
        return result;
    }
    
    /**
     * Helper API endpoint to return GIS digestible polygons 
     * @param topo50SheetNames
     * @param qmapSheetNames
     * @return 
     */
    @RequestMapping(value = "/mapsheets/geojson", produces = "application/json")
    String getMapsheetsGeoJson(
            @RequestParam(value = "topo50sheet") String[] topo50SheetNames, 
            @RequestParam(value = "qmapSheet") String[] qmapSheetNames)   {
        return newSiteService.getQMAPSheetsGeoJson(Arrays.asList(qmapSheetNames));
    }
    
    
/**
 * Query the site database for all points within a given distance of a point,
 * returning the candidates points and the distance.
 * 
 * @param easting
 * @param northing
 * @param distance
 * @param epsg
 * @param response
 * @return
 * @throws InvalidLatLonFormat
 * @throws InvalidOrigCoordinate
 * @throws FactoryException
 * @throws MismatchedDimensionException
 * @throws TransformException
 * @throws JsonProcessingException 
 */
    @RequestMapping("/sites/closeto")
    public List<SiteProximity> getAllSitesClose(@RequestParam(value = "easting") double easting, @RequestParam(value = "northing") double northing,
            @RequestParam(value = "metres") double distance, @RequestParam(value = "EPSG") int epsg, HttpServletResponse response)
            throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        Point point = OrigCoord.MakeGeomPt(easting, northing, epsg);
        distance = distance/111120; // approximately convert meters into degrees. 
        return newSiteService.findCloseTo(point, distance);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/islands")
    public List<Island> findAllIslands() {
        return islandService.findAll();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/qmapsheets")
    public List<MapSheetPayload> findAllQmapSheets() {
        return qmapService.findAll();
    }
}
