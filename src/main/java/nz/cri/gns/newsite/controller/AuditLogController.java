/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.controller;

import java.util.List;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author sitikond
 */
@RestController
@RequestMapping("/api/v1")
public class AuditLogController {
    
    @Autowired
    AuditLogService auditLogService;
    
    @RequestMapping("/sites/{siteid}/auditlogs")
    public List<AuditLog> getAllAuditLogs(@PathVariable int siteid) {
        return auditLogService.findAuditLogsBySiteId(siteid);
    }
    
}
