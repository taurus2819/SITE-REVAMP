/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.service;

import java.util.List;
import javax.transaction.Transactional;
import nz.cri.gns.newsite.model.SiteUsage;
import nz.cri.gns.newsite.repository.SiteUsageRespository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author scaddenp
 */
@Service
@Scope("singleton")
public class SiteUsageServiceImpl implements SiteUsageService{

    private static final Logger logger = LoggerFactory.getLogger(SiteUsageServiceImpl.class);
    @Autowired
    SiteUsageRespository siteUsageRepository;
    
    @Override
    public SiteUsage registerUsage(SiteUsage siteUsage) {
        return siteUsageRepository.save(siteUsage);
    }

    @Override
    public List<SiteUsage> findUsageBySiteId(int siteid) {
       return siteUsageRepository.findAllBySiteId(siteid);
    }


    @Override
    @Transactional
    public boolean unregisterUsage(SiteUsage siteUsage) {
        long count = siteUsageRepository.deleteBySiteIdAndUsedBy(siteUsage.getSiteId(),siteUsage.getUsedBy());
        return (count==1);
    }
    
}
