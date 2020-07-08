package nz.cri.gns.newsite.controller;

/**
 *
 * @author sitikond
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParser;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.service.NewSiteService;
import nz.cri.gns.newsite.utils.OrigCoord;
import org.json.JSONObject;

@RestController
public class NewSiteController {

	@Autowired
	NewSiteService newSiteService;
	
	@RequestMapping("/sites/origsysid/{oid}")
	public List<SiteModel> getAllSites(@PathVariable int oid){
            return newSiteService.findByOrigSystemId(oid);				
	}
	
	@RequestMapping("/sites/{id}")
	public SiteModel getSite(@PathVariable int id) {
            return newSiteService.find(id);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/site")
	public SiteModel addSite(@RequestBody SiteModel site, HttpServletResponse response) {
            site = newSiteService.insert(site);
            if(site.getLat() == 0.0 && site.getLon() == 0.0){
                String epsgFormatInfo = OrigCoord.getEpsgInfoJsonString(site.getOrigSystemId(), site.getOrigCoord());
                JSONObject obj = new JSONObject(epsgFormatInfo);
                    if(obj.getString("format").equals("DD") || obj.getString("format").equals("EN")){
                    site.setLat(Double.parseDouble(obj.getString("latitude")));
                    site.setLon(Double.parseDouble(obj.getString("longitude")));
                    site = updateSite(site, site.getSiteId());
                }
            }                
            response.setStatus(HttpServletResponse.SC_CREATED);
            return site;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="/site/{id}")
	public SiteModel updateSite(@RequestBody SiteModel site, @PathVariable int id) {
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
            newSiteService.update(siteById);
            return siteById;
	}
        
        @RequestMapping(value = "/sites/format/{id}", method = {RequestMethod.GET}, produces = "application/json")
        public String epsgInfo(@PathVariable int id){
            SiteModel site = getSite(id);
            System.out.println("Site in JSON = " + site);
            return OrigCoord.getEpsgInfoJsonString(site.getOrigSystemId(), site.getOrigCoord());
        }    
        
        
    
}
