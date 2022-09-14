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
