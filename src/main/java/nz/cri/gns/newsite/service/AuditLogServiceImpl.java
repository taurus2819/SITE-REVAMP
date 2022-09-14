/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.repository.AuditLogRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author sitikond
 */
@Service
@Scope("singleton")
public class AuditLogServiceImpl implements AuditLogService{

    @Autowired
    AuditLogRespository auditLogRespository;
    
    @Override
    public AuditLog insert(AuditLog a) {
        return auditLogRespository.save(a);
    }

    @Override
    public List<AuditLog> findAuditLogsBySiteId(int siteid) {
        List<AuditLog> auditLogsBySiteId = auditLogRespository.findAllByAuditSiteId(siteid);
        return auditLogsBySiteId;
    }
    
}
