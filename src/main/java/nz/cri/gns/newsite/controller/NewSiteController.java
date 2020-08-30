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
        AuditLog newAuditLog = auditLogCreator(site, newlyCreatedSiteId);
        site.addAuditLog(newAuditLog);
        auditLogService.insert(newAuditLog);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return site;
//        } catch (Exception e) {
//            throw new InvalidOrigCoordinate("Not a valid format#$%");
//        }
    }

    private AuditLog auditLogCreator(SiteModel site, int newlyCreatedSiteId) throws JsonProcessingException, JSONException {
        JSONObject logTimestampMsg;
        logTimestampMsg = new JSONObject();
        logTimestampMsg.put("timestamp", new Date());
        logTimestampMsg.put("info",site.getAuditLogInfoMsg());
        ObjectMapper mapper = new ObjectMapper();
        AuditLog newAuditLog = new AuditLog(newlyCreatedSiteId, mapper.readTree(logTimestampMsg.toString())); //, site);
        return newAuditLog;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "/site/{id}")
    public SiteModel updateSite(@RequestBody SiteModel site, @PathVariable int id) throws JsonProcessingException {
        final SiteModel siteById = newSiteService.find(id);
        siteById.setSiteName(site.getSiteName());
        siteById.setLat(site.getLat());
        siteById.setLon(site.getLon());
        siteById.setMethodId(site.getMethodId());
        siteById.setAccuracy(site.getAccuracy());
        siteById.setDirections(site.getDirections());
        siteById.setOrigSystemId(site.getOrigSystemId());
        siteById.setOrigCoord(site.getOrigCoord());
        siteById.setHeight(site.getHeight());
        siteById.setHeightMethodId(site.getHeightMethodId());
        siteById.setHeightAccuracy(site.getHeightAccuracy());
        siteById.setCountryCode(site.getCountryCode());
        siteById.setFlag(site.getFlag());
        siteById.setComment(site.getComment());        
        AuditLog newAuditLog = auditLogCreator(siteById, id);
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

}
