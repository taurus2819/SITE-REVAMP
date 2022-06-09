package nz.cri.gns.newsite.controller;

/**
 *
 * @author scaddenp
 */

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nz.cri.gns.newsite.model.SiteUsage;
import nz.cri.gns.newsite.service.SiteUsageService;

@RestController
@RequestMapping("/site/api/v1/sites/usage")
public class SiteUsageController {


    @Autowired
    SiteUsageService siteUsageService;


    @RequestMapping("/{siteId}")
    public List<SiteUsage> getSiteUsage(@PathVariable int siteId) {
        return siteUsageService.findUsageBySiteId(siteId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public SiteUsage registerSiteUsage(@RequestBody SiteUsage siteUsage, HttpServletResponse response) {
        try {
            SiteUsage s = siteUsageService.registerUsage(siteUsage);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return s;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/unregister")
    public void unregister(@RequestBody SiteUsage siteUsage, HttpServletResponse response) {
        if (siteUsageService.unregisterUsage(siteUsage) ) {
          response.setStatus(HttpServletResponse.SC_OK);
        } else {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);            
        }
    }

}
