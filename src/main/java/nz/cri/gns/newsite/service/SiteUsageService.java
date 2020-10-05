/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.SiteUsage;

/**
 *
 * @author scaddenp
 */
public interface SiteUsageService {
    SiteUsage registerUsage(SiteUsage siteUsage);
    boolean unregisterUsage(SiteUsage siteUsage);
    List<SiteUsage> findUsageBySiteId(int siteid);
}
