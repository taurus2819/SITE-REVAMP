package nz.cri.gns.newsite.controller;

/**
 *
 * @author sitikond
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteModelInput;
import nz.cri.gns.newsite.service.AuditLogService;
import nz.cri.gns.newsite.service.NewSiteService;
import nz.cri.gns.newsite.utils.ConversionToWgs84;
import nz.cri.gns.newsite.utils.OrigCoord;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/v1")
public class NewSiteController {

    @Autowired
    NewSiteService newSiteService;

    @Autowired
    AuditLogService auditLogService;

    @RequestMapping("/sites/origsysid/{oid}")
    public List<SiteModel> getAllSites(@PathVariable int oid) {
        return newSiteService.findByOrigSystemId(oid);
    }

    @RequestMapping("/sites/{id}")
    public SiteModel getSite(@PathVariable int id) {
        return newSiteService.find(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/site")
    public SiteModel addSite(@RequestBody SiteModelInput siteInput, HttpServletResponse response) throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
//    try {
        SiteModel site = siteInput.toSiteModel();
        site = newSiteService.insert(site);
        /*            if (site.getLat() == 0.0 && site.getLon() == 0.0) {
                String epsgFormatInfo = OrigCoord.getEpsgInfoJsonString(site.getOrigSystemId(), site.getOrigCoord());
                JSONObject obj = new JSONObject(epsgFormatInfo);
                if (obj.getString("format").equals("EN")) {
                    site.setLat(Double.parseDouble(obj.getString("latitude")));
                    site.setLon(Double.parseDouble(obj.getString("longitude")));
                    site = updateSite(site, site.getSiteId());
                } else if (obj.getString("format").equals("DD")) {
                    ConversionToWgs84 llToWgs84 = new ConversionToWgs84(obj.getDouble("latitude"), obj.getDouble("longitude"));
                    site.setLat(llToWgs84.getConvertedLat());
                    site.setLon(llToWgs84.getConvertedLon());
                    site = updateSite(site, site.getSiteId());
                }
            }
         */
        int newlyCreatedSiteId = site.getSiteId();
        List<String> siteinfoBefore = new ArrayList<>();
        siteinfoBefore.add("message: Newly Created");
        AuditLog newAuditLog = auditLogCreator(site, newlyCreatedSiteId, siteinfoBefore);
        site.addAuditLog(newAuditLog);
        auditLogService.insert(newAuditLog);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return site;
//        } catch (Exception e) {
//            throw new InvalidOrigCoordinate("Not a valid format#$%");
//        }
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
        List<String> siteinfoBefore = new ArrayList<String>();
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

    @RequestMapping("/sites/query")
    public List<SiteModel> getAllSitesWithin(@RequestBody SiteModelInput siteInput, @RequestParam(value = "minNorth") double minNorth, @RequestParam(value = "minEast") double minEast,
            @RequestParam(value = "maxNorth") double maxNorth, @RequestParam(value = "maxEast") double maxEast, HttpServletResponse response)
            throws InvalidLatLonFormat, InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException {
        SiteModel site = siteInput.toSiteModel();
        Point2D latlng1 = extractLatLong(minEast, maxNorth);
        Point2D latlng2 = extractLatLong(maxEast, maxNorth);
        Point2D latlng3 = extractLatLong(minEast, minNorth);
        Point2D latlng4 = extractLatLong(maxEast, minNorth);
        return newSiteService.listAllSites();
    }

    private Point2D extractLatLong(double easting, double northing) throws TransformException, FactoryException, MismatchedDimensionException {
        Point2D latlng;
        Point2D inputPt = new Point2D.Double();
        inputPt.setLocation(easting, northing);
        latlng = OrigCoord.toWGS84(27200, inputPt);
        return latlng;
    }

}
