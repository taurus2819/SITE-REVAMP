package nz.cri.gns.newsite.repository;

/**
 *
 * @author sitikond
 */

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.SiteModel;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NewSiteRepository extends JpaRepository<SiteModel, Integer>{
	
	//getAllSites()
	//getSite(Integer id)
	//updateSite(Site s)
	//deleteSite(Integer id)
//    @Query("select s from site_proposed s where s.orig_system_id = 16")
    public List<SiteModel> findAllByOrigSystemId(int oid) ;

    @Query(value = "Select s from #{#entityName} s where within(s.shape, :bounds )= true")
    public List<SiteModel> findWithinBounds(Geometry bounds);
    
    @Query(value = "Select s from #{#entityName} s INNER JOIN s.users where dwithin(s.shape, :point, :distance)= true")
    public List<SiteModel> findCloseTo(Geometry point, double distance);


}
