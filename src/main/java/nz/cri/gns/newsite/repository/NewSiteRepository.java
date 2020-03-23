package nz.cri.gns.newsite.repository;

/**
 *
 * @author sitikond
 */

import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.SiteModel;

public interface NewSiteRepository extends JpaRepository<SiteModel, Integer>{
	
	//getAllSites()
	//getSite(Integer id)
	//updateSite(Site s)
	//deleteSite(Integer id)

}
