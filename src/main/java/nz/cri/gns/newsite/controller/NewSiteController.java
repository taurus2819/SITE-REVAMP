package nz.cri.gns.newsite.controller;

/**
 *
 * @author sitikond
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import jakarta.servlet.http.HttpServletResponse;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import nz.cri.gns.newsite.model.*;
import nz.cri.gns.newsite.model.Site.SiteMode;

import nz.cri.gns.newsite.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.utils.CoordinateConverter;
import nz.cri.gns.newsite.utils.ObjectMapperWrapper;
import nz.cri.gns.newsite.utils.OrigCoord;
import nz.cri.gns.newsite.utils.OrigCoordCheckResult;
import nz.cri.gns.newsite.utils.OrigCoord.OrigCoordDetail;
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
@RequestMapping("/site/api/v1")
@CrossOrigin(origins="*", allowedHeaders="*")
public class NewSiteController {

    @Autowired
    NewSiteService newSiteService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    SiteUsageService siteUsageService;
    
    @Autowired
    IslandService islandService;
    
    @Autowired
    QMAPService qmapService;
    
    @Autowired
    MethodService methodService;
    
    @Autowired
    MasterFileService masterFileService;

    private static final Logger log = LoggerFactory.getLogger(NewSiteController.class);

    @RequestMapping(value = "/sites/origsysid/{oid}", method = RequestMethod.GET)
    public List<SiteModel> getAllSites(@PathVariable int oid) {
        return newSiteService.findByOrigSystemId(oid);
    }


    @RequestMapping(value = "/sites/{id}", method = RequestMethod.GET)
    public SiteModel getSite(@PathVariable int id) {
        return newSiteService.find(id);
    }

    @RequestMapping(value = "/sites/{id}/details", method = RequestMethod.GET)
    public SiteDetailed getSiteDetails(@PathVariable int id) {
        return newSiteService.findDetails(id);
    }
    
    @RequestMapping(value = "/site", method = RequestMethod.POST)
    public SiteModel addSite(@RequestBody SiteModelInput siteInput, HttpServletResponse response) throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        if (siteInput == null) {
            throw new IllegalArgumentException("siteInput is null!");
        }
        log.info("Received siteInput: {}", siteInput);
        SiteModel site = siteInput.toSiteModel();
        site = newSiteService.insert(site);
        int newlyCreatedSiteId = site.getSiteId();
        List<String> siteinfoBefore = new ArrayList<>();
        siteinfoBefore.add("message: Newly Created");
        AuditLog newAuditLog = auditLogCreator(site, newlyCreatedSiteId, siteinfoBefore);
        site.addAuditLog(newAuditLog);
        auditLogService.insert(newAuditLog);
        SiteUsage siteUser = new SiteUsage(newlyCreatedSiteId, siteInput.getClientUser());
        site.addSiteUsage(siteUser);
        siteUsageService.registerUsage(siteUser);
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
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

        AuditLog newAuditLog = new AuditLog(createdOrModifiedSiteId, mapper.readTree(logTimestampMsg.toString())); //, site);
        return newAuditLog;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/site/{id}", method = RequestMethod.PUT)
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

    @RequestMapping(value = "/sites/format/{id}", method = RequestMethod.GET, produces = "application/json")
    public String getEpsgInfo(@PathVariable int id) {
        SiteModel site = getSite(id);
        System.out.println("Site in JSON = " + site);
        return OrigCoord.getEpsgInfoJsonString(site.getOrigSystemId(), site.getOrigCoord());
    }

    @RequestMapping(value = "/site/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id, HttpServletResponse response) {
        newSiteService.delete(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    @RequestMapping(value = "/site/{id}/masterfile", method = RequestMethod.GET)
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
     * @param siteMode
 * @param response
 * @return
 * @throws InvalidLatLonFormat
 * @throws InvalidOrigCoordinate
 * @throws FactoryException
 * @throws MismatchedDimensionException
 * @throws TransformException
 * @throws JsonProcessingException 
 */
    @RequestMapping(value = "/sites/query/bbox",method = RequestMethod.GET)
    public List<Site> getAllSitesWithin(
            @RequestParam(value = "minNorth") double minNorth, 
            @RequestParam(value = "minEast") double minEast,
            @RequestParam(value = "maxNorth") double maxNorth, 
            @RequestParam(value = "maxEast") double maxEast, 
            @RequestParam(value = "EPSG") int epsg, 
            @RequestParam(value = "siteMode", required=false) String siteMode,
            HttpServletResponse response)
            throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {

        return newSiteService.findWithinBounds(CoordinateConverter.convertBbox(minNorth, minEast, maxNorth, maxEast, epsg), (siteMode != null && siteMode.equals("complete") ? SiteMode.COMPLETE : SiteMode.ID_ONLY));
    }
    
    @RequestMapping(value = "/sites/query/mapsheets", method = RequestMethod.GET)
    List<Site> getAllSitesWithinMapSheets(
            @RequestParam(value = "topo50sheet", required=false) String[] topo50SheetNames, 
            @RequestParam(value = "qmapSheet", required=false) String[] qmapSheetNames,
            @RequestParam(value = "nzmgSheet", required=false) String[] nzmgSheetNames,
            @RequestParam(value = "countryCode", required=false) String[] countryCodes,
            @RequestParam(value = "island", required=false) String[] islandNames,
            @RequestParam(value = "siteMode", required=false) String siteMode)   {
        
        long start = System.nanoTime();
        List<Site> result = newSiteService.findWithinMapSheets(
                topo50SheetNames != null ? Arrays.asList(topo50SheetNames) : new ArrayList<>(), 
                qmapSheetNames != null ? Arrays.asList(qmapSheetNames) : new ArrayList<>(),
                nzmgSheetNames != null ? Arrays.asList(nzmgSheetNames) : new ArrayList<>(),
                countryCodes != null ? Arrays.asList(countryCodes) : new ArrayList<>(),
                islandNames != null ? Arrays.asList(islandNames) : new ArrayList<>(),
                (siteMode != null && siteMode.equals("complete") ? SiteMode.COMPLETE : SiteMode.ID_ONLY)); 
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.err.println(String.format("API response time: %d ms", timeElapsed/1000000));
        
        return result;
    }
    
    @RequestMapping(value = "/sites/query/mapsheets/qmap", method = RequestMethod.GET)
    List<Site> getAllSitesWithinQMapSheets( 
            @RequestParam(value = "qmapSheet") String[] qmapSheetNames, 
            @RequestParam(value = "siteMode", required=false) String siteMode)   {
       
        long start = System.nanoTime();
        List<Site> result = newSiteService.findWithinQMAPSheets(Arrays.asList(qmapSheetNames), (siteMode != null && siteMode.equals("complete") ? SiteMode.COMPLETE : SiteMode.ID_ONLY));   
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.err.println(String.format("API response time: %d ms", timeElapsed/1000000));
        
        return result;
    }
    
    @RequestMapping(value = "/sites/query/mapsheets/topo50", method = RequestMethod.GET)
    List<Site> getAllSitesWithiTopo50Sheets(
            @RequestParam(value = "topo50sheet") String[] topo50SheetNames,
            @RequestParam(value = "siteMode", required=false) String siteMode)   {
        
        long start = System.nanoTime();
        List<Site> result = newSiteService.findWithinTopo50Sheets(Arrays.asList(topo50SheetNames), (siteMode != null && siteMode.equals("complete") ? SiteMode.COMPLETE : SiteMode.ID_ONLY));
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
    @RequestMapping(value = "/mapsheets/geojson", method = RequestMethod.GET, produces = "application/json")
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
    @RequestMapping(value = "/sites/closeto", method = RequestMethod.GET)
    public List<SiteProximity> getAllSitesClose(@RequestParam(value = "easting") double easting, @RequestParam(value = "northing") double northing,
            @RequestParam(value = "metres") double distance, @RequestParam(value = "EPSG") int epsg, HttpServletResponse response)
            throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        Point point = OrigCoord.MakeGeomPt(easting, northing, epsg);
        distance = distance/111120; // approximately convert meters into degrees. 
        return newSiteService.findCloseTo(point, distance);
    }
    
    @RequestMapping(value = "/islands", method = RequestMethod.GET)
    public List<Island> findAllIslands() {
        return islandService.findAll();
    }
    
    @RequestMapping(value = "/qmapsheets", method = RequestMethod.GET)
    public List<MapSheetPayload> findAllQmapSheets() {
        return qmapService.findAll();
    }
    
    @RequestMapping(value = "/methods", method = RequestMethod.GET)
    public List<DatumMethod> findAllMethods() {
        return methodService.findAll();
    }
    
    @RequestMapping(value = "/methods/{id}", method = RequestMethod.GET)
    public DatumMethod findMethod(@PathVariable int id) {
        return methodService.find(id);
    }
    
    @RequestMapping(value = "/methods/name/{name}", method = RequestMethod.GET)
    public List<DatumMethod> findMethod(@PathVariable String name) {
        return methodService.findByName(name);
    }
    
    @RequestMapping(value = "/legacy/datum/{datumId}", method = RequestMethod.GET)
    public OrigCoordDetail findCoordDetailsForLegacyDatum(@PathVariable Integer datumId) {
        return OrigCoord.getOrigCoordDetails(datumId);
    }
    
    @RequestMapping(value = "/legacy/origsystemid/", method = RequestMethod.GET)
    public Integer findOrigSystemId(@RequestParam(value = "epsg") int epsg,
                                                  @RequestParam(value = "format") String format) {
        return OrigCoord.getOrigSystemId(epsg,format);
    }

    
    /**
     * Utility for converting point location between arbitrary coordinate systems
     * defined by EPSG codes.
     * 
     * @param east - the east coordinate. For lat/long systems, this is Longitude
     * @param north - the north coordinate, for lat/long systems, this is latitude
     * @param inEpsg - epsg no. of the input coordinate system
     * @param outEpsg - epsg no. of the output coordinate system
     * @return - a Point2D representation of the input point converted to outEPSG
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @RequestMapping(value = "/util/convert", method = RequestMethod.GET)
    public Point2D convertPoint(
            @RequestParam(value = "east") double east,
            @RequestParam(value = "north") double north,
            @RequestParam(value = "inEpsg") int inEpsg,
            @RequestParam(value = "outEpsg") int outEpsg
        ) throws FactoryException, MismatchedDimensionException, TransformException {    
        Point2D input = new Point2D.Double(east,north);
        return OrigCoord.convertEpsg(inEpsg, outEpsg, input);
    }
/**
 *
 * @param epsg  - EPSG of original coordinate system
 * @param format - Format of original coordinate system (either gridref, DD, DMS, or EN)
 * @param easting - String holding easting or longitude or full gridref
 * @param northing - String holding northing or latitude, blacnk for a gridref
 * @return - message (for errors) and point converted to WGS84 lnglat
 */
    @RequestMapping(value = "/util/validCoord", method = RequestMethod.GET)
    public OrigCoordCheckResult validOrigCoordinateCheck(
            @RequestParam(value = "epsg") int epsg,
            @RequestParam(value = "format") String format,
            @RequestParam(value = "easting") String easting,
            @RequestParam(value = "northing") String northing
        )  {
        String message = "";
        Point2D.Double lnglat = new Point2D.Double();
        try {
            lnglat = (Point2D.Double) OrigCoord.convertOrigCoordToWGS(epsg, format, easting, northing);
        } catch (InvalidLatLonFormat ex) {
            message = "Invalid lat/lon format: " + ex.getMessage();
        } catch (InvalidOrigCoordinate ex) {
            message = "Invalid format: " + ex.getMessage();
        } catch (FactoryException ex) {
            message = "EPSG " + epsg +" is not valid";
        } catch (MismatchedDimensionException ex) {
            message = "Invalid coordinate format";
        } catch (TransformException ex) {
            message = "EPSG " + epsg +" is not valid: " + ex.getMessage();
        } catch (NumberFormatException ex) {
            message = "Format specified is invalid. Could not convert to numeric";
        }
        return new OrigCoordCheckResult(message,lnglat);
    }

}
