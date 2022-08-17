package nz.cri.gns.newsite.service;

import nz.cri.gns.newsite.model.SiteModel;


/**
 *
 * @author sorenh
 */
public interface MasterFileService {
    public Integer getMasterFile(SiteModel site, int registrationAreaId, boolean isbacklogFeature);
}
