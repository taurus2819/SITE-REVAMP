package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import nz.cri.gns.newsite.exception.ResourceMissingException;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.repository.NewSiteRepository;

@Service
@Scope("singleton")
public class NewSiteServiceImpl implements NewSiteService{

    private static final Logger logger = LoggerFactory.getLogger(NewSiteServiceImpl.class);
    private List<SiteModel> _sites = new ArrayList<SiteModel>();
	
	@Autowired
	NewSiteRepository newSiteRepository;
	
	@Override
	public SiteModel insert(SiteModel s) {
            SiteModel newSite = newSiteRepository.save(s);
            logger.info("NewSite = " + newSite.toString());
            return newSite;
	}

	@Override
	public SiteModel update(SiteModel s) {
		retrieveWithNullCheck(s.getSiteId());
                logger.info("Update = " + s.toString());
		return newSiteRepository.save(s);
	}

	private SiteModel retrieveWithNullCheck(int i) {
		SiteModel site = newSiteRepository.findById(i)
							.orElseThrow(
//                                                                logger.error("Site with id " + i + " not found");
                                                                ()->new ResourceMissingException("Site with id " + i + " not found"));		
                logger.info(site.toString());
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
	}

	@Override
	public List<SiteModel> listAllSites() {
		return newSiteRepository.findAll();		
	}

}
