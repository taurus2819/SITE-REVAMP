package nz.cri.gns.newsite.model;

import lombok.Getter;
import lombok.Setter;
import nz.cri.gns.newsite.utils.NZMS262;
import nz.cri.gns.newsite.utils.NZMS260;
import nz.cri.gns.newsite.utils.QMAPSheet;
import nz.cri.gns.newsite.utils.Topo50;

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
    
    public String getTopo50Sheet()    {
        Topo50 topo50 = Topo50.getInstance();
        return topo50.getMapsheet(model.getShape());
    }
    
    public String getQMAPSheet()    {
        QMAPSheet qmapSheet = QMAPSheet.getInstance();
        return qmapSheet.getMapsheet(model.getShape());
    }
    
    public String getNZMS260Sheet()    {
        NZMS260 nzms260Sheet = NZMS260.getInstance();
        return nzms260Sheet.getMapsheet(model.getShape());
    }
    
    public String getNZMS262Sheet()    {
        NZMS262 nzmg262Sheet = NZMS262.getInstance();
        return nzmg262Sheet.getMapsheet(model.getShape());
    }
}
