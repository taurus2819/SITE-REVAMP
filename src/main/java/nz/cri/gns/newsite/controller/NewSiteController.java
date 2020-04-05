package nz.cri.gns.newsite.controller;

/**
 *
 * @author sitikond
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.service.NewSiteService;

@RestController
public class NewSiteController {

	@Autowired
	NewSiteService newSiteService;
	
	@RequestMapping("/sites")
	public List<SiteModel> getAllSites(){
            return newSiteService.findAll();				
	}
	
	@RequestMapping("/sites/{id}")
	public SiteModel getSite(@PathVariable int id) {
            return newSiteService.find(id);
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/site")
	public void addSite(@RequestBody SiteModel site) {
            newSiteService.insert(site);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="/site/{id}")
	public void updateSite(@RequestBody SiteModel site, @PathVariable int id) {
            final SiteModel siteById = newSiteService.find(id);
            siteById.setSiteName(site.getSiteName());
            siteById.setLat(site.getLat());
            siteById.setLon(site.getLon());
            siteById.setMethodId(site.getMethodId());
            siteById.setAccuracy(site.getAccuracy());
            siteById.setDirections(site.getDirections());
            siteById.setOrigSystemId(site.getOrigSystemId());
            siteById.setHeight(site.getHeight());
            siteById.setHeightMethodId(site.getHeightMethodId());
            siteById.setHeightAccuracy(site.getHeightAccuracy());
            siteById.setCountryCode(site.getCountryCode());
            siteById.setFlag(site.getFlag());
            siteById.setComment(site.getComment());
            newSiteService.update(siteById);
	}
}
