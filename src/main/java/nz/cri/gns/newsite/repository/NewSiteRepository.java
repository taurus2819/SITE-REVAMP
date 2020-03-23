package nz.cri.gns.newsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.SiteModel;

public interface NewSiteRepository extends JpaRepository<SiteModel, Integer>{
	
	//getAllSites()
	//getSite(Integer id)
	//updateSite(Site s)
	//deleteSite(Integer id)

}
