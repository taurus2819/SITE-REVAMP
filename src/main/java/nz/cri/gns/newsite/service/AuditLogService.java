/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.audits.AuditLog;

/**
 *
 * @author sitikond
 */
public interface AuditLogService {
    AuditLog insert(AuditLog a);
    List<AuditLog> findAuditLogsBySiteId(int siteid);
}
