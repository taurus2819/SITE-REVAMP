package nz.cri.gns.newsite.repository;

/**
 *
 * @author sorenh
 */

import java.util.List;
import nz.cri.gns.newsite.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.SiteId;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;

public interface NewSiteIdRepository extends JpaRepository<SiteId, Integer>{
	
    @Query(value = "Select s from #{#entityName} s where within(s.shape, :bounds )= true")
    public List<Site> findWithinBounds(Geometry bounds);
    
}
