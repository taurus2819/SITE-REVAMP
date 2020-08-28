/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.repository;

import java.util.List;
import nz.cri.gns.newsite.audits.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author sitikond
 */
public interface AuditLogRespository extends JpaRepository<AuditLog, Integer>{
    //@Query("select a from sc.audit_log a where a.site_id = ?1")         //@Query("select audit_log.log_info from audit_log where audit_log.site_id = ?1")
    public List<AuditLog> findAllByAuditSiteId(int siteId);
}
