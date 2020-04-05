package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import nz.cri.gns.newsite.exception.ResourceMissingException;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.repository.NewSiteRepository;

@Service
@Scope("singleton")
public class NewSiteServiceImpl implements NewSiteService{

//	private SiteModel siteModel1 = new SiteModel(51955, "GNS Physical Location Site, Dunedin", -45.864369921, 170.513135754, 3, 10.0, "764 Cumberland Street, Dunedin", 38, "2316763|5479842", 44.0, 3, 10.0, "NZ", 0, "", "0101000020E6100000D33AAC9B6B506540A92B6FACA3EE46C0");
//	private SiteModel siteModel2 = new SiteModel(51956, "GNS Physical Location Site, Dunedin", -45.864369921, 170.513135754, 3, 10.0, "764 Cumberland Street, Dunedin", 38, "2316763|5479842", 44.0, 3, 10.0, "NZ", 0, "", "0101000020E6100000D33AAC9B6B506540A92B6FACA3EE46C0");
//	private SiteModel siteModel3 = new SiteModel(51957, "GNS Physical Location Site, Dunedin", -45.864369921, 170.513135754, 3, 10.0, "764 Cumberland Street, Dunedin", 38, "2316763|5479842", 44.0, 3, 10.0, "NZ", 0, "", "0101000020E6100000D33AAC9B6B506540A92B6FACA3EE46C0");
	private List<SiteModel> _sites = new ArrayList<SiteModel>();
	
	@Autowired
	NewSiteRepository newSiteRepository;
	
	@Override
	public SiteModel insert(SiteModel s) {
            SiteModel newSite = newSiteRepository.save(s);
            System.out.println("NewSite id = " + newSite.getSiteId());
            return newSite;
	}

	@Override
	public SiteModel update(SiteModel s) {
		retrieveWithNullCheck(s.getSiteId());
		return newSiteRepository.save(s);
	}

	private SiteModel retrieveWithNullCheck(int i) {
		SiteModel site = newSiteRepository.findById(i)
							.orElseThrow(()->new ResourceMissingException("Site with id " + i + "not found"));		
		return site;
	}

	@Override
	public void delete(int id) {
		retrieveWithNullCheck(id);
		newSiteRepository.deleteById(id);
	}
	
	@Override
	public SiteModel find(int id) {
		return retrieveWithNullCheck(id);
	}

	@Override
	public List<SiteModel> findAll() {
		List<SiteModel> sites = new ArrayList<>();
		newSiteRepository.findAll().forEach(sites::add);
                return sites;
//		_sites.add(siteModel1);
//		_sites.add(siteModel2);
//		_sites.add(siteModel3);
//		return _sites;
	}

	@Override
	public List<SiteModel> listAllSites() {
		return newSiteRepository.findAll();		
	}

}
