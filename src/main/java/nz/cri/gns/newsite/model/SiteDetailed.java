package nz.cri.gns.newsite.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sorenh
 */
@Getter @Setter
public class SiteDetailed {
    
    private SiteModel model;
    
    public SiteDetailed(SiteModel sm)   {
        this.model = sm;
    }
    
    public String getQmapSheet()    {
        return "Coming up soson.";
    }
}
