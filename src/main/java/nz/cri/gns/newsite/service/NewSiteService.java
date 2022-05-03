package nz.cri.gns.newsite.service;

/**
 *
 * @author sitikond
 */
import java.util.List;

import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteProximity;
import org.locationtech.jts.geom.Geometry;

public interface NewSiteService {
	
	SiteModel insert(SiteModel s);
	SiteModel update(SiteModel s);
	void delete(int id);
	SiteModel find(int id);
	List<SiteModel> findByOrigSystemId(int oid);
	List<SiteModel> findWithinBounds(Geometry bounds);
	List<SiteProximity> findCloseTo(Geometry Point, double distance);
	List<SiteModel> listAllSites();
        List<SiteModel> findWithinMapSheets(List<String> topo50Sheets, List<String> qmapSheets);
        List<SiteModel> findWithinTopo50Sheets(List<String> sheetnames);
        List<SiteModel> findWithinQMAPSheets(List<String> sheetnames);
        String getQMAPSheetsGeoJson(List<String> qmapSheets);

//	List<SiteModel> findAll(Integer origSystemID);
	
}
