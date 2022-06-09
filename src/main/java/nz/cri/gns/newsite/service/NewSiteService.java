package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.List;
import nz.cri.gns.newsite.model.Site;
import nz.cri.gns.newsite.model.Site.SiteMode;
import nz.cri.gns.newsite.model.SiteDetailed;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteProximity;
import org.locationtech.jts.geom.Geometry;

public interface NewSiteService {
	
	SiteModel insert(SiteModel s);
	SiteModel update(SiteModel s);
	void delete(int id);
	SiteModel find(int id);
	List<SiteModel> findByOrigSystemId(int oid);
	List<Site> findWithinBounds(Geometry bounds, SiteMode mode);
	List<SiteProximity> findCloseTo(Geometry Point, double distance);
	List<SiteModel> listAllSites();
        List<Site> findWithinMapSheets(List<String> topo50Sheets, List<String> qmapSheets, List<String> nzmgSheets, List<String> islands, SiteMode mode);
        List<Site> findWithinIslands(List<String> islandNames, SiteMode mode);
        List<Site> findWithinTopo50Sheets(List<String> sheetnames, SiteMode mode);
        List<Site> findWithinQMAPSheets(List<String> sheetnames, SiteMode mode);
        List<Site> findWithinNZMGSheets(List<String> sheetnames, SiteMode mode);
        String getQMAPSheetsGeoJson(List<String> qmapSheets);
        
        SiteDetailed findDetails(int id);

//	List<SiteModel> findAll(Integer origSystemID);
	
}
