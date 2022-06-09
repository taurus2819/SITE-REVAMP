package nz.cri.gns.newsite.model;

import java.io.Serializable;

/**
 * Interface used to be able to return differently detailed versions of a SITE record (e.g. complete, ID-only, ...)
 * @author sorenh
 */
public interface Site extends Serializable {
    
    public enum SiteMode {
        COMPLETE,
        ID_ONLY
    } 
    
    public Integer getSiteId();
}
