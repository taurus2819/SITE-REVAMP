package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.List;

import nz.cri.gns.newsite.model.SiteModel;
import org.locationtech.jts.geom.Geometry;

public interface NewSiteService {
	
	SiteModel insert(SiteModel s);
	SiteModel update(SiteModel s);
	void delete(int id);
	SiteModel find(int id);
	List<SiteModel> findByOrigSystemId(int oid);
	List<SiteModel> findWithinBounds(Geometry bounds);
	List<SiteModel> findCloseTo(Geometry Point, double distance);
	List<SiteModel> listAllSites();
//	List<SiteModel> findAll(Integer origSystemID);
	
}
