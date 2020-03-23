package nz.cri.gns.newsite.service;

import java.util.List;

import nz.cri.gns.newsite.model.SiteModel;

public interface NewSiteService {
	
	SiteModel insert(SiteModel s);
	SiteModel update(SiteModel s);
	void delete(int id);
	SiteModel find(int id);
	List<SiteModel> findAll();
	List<SiteModel> listAllSites();
//	List<SiteModel> findAll(Integer origSystemID);
	
}
