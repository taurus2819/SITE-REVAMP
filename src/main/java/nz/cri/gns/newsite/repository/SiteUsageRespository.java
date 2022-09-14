package nz.cri.gns.newsite.repository;

import java.util.List;
import nz.cri.gns.newsite.model.SiteUsage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author scaddenp
 */
public interface SiteUsageRespository extends JpaRepository<SiteUsage, Integer>{
    public List<SiteUsage> findAllBySiteId(int siteId);
    
    public long deleteBySiteIdAndUsedBy(Integer siteId, String UsedBy);
}
