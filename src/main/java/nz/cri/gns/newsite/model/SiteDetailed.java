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
    
    private Island island;
    
    public SiteDetailed(SiteModel sm)   {
        this.model = sm;
    }
    
    public String getTopo50Sheet()    {
        Topo50 topo50 = Topo50.getInstance();
        return ((model.getShape() != null) ? topo50.getMapsheet(model.getShape()): null);
    }
    
    public String getQMAPSheet()    {
        QMAPSheet qmapSheet = QMAPSheet.getInstance();
        return ((model.getShape() != null) ? qmapSheet.getMapsheet(model.getShape()) : null);
    }
    
    public String getNZMS260Sheet()    {
        NZMS260 nzms260Sheet = NZMS260.getInstance();
        return ((model.getShape() != null) ? nzms260Sheet.getMapsheet(model.getShape()) : null) ;
    }
    
    public String getNZMS262Sheet()    {
        NZMS262 nzmg262Sheet = NZMS262.getInstance();
        return ((model.getShape() != null) ? nzmg262Sheet.getMapsheet(model.getShape()) : null);
    }
}
