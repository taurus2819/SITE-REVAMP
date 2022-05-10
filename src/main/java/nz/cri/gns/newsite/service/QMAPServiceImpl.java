package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.MapSheetPayload;
import nz.cri.gns.newsite.utils.QMAPSheet;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author sorenh
 */
@Service
@Scope("singleton")
@Configurable
public class QMAPServiceImpl implements QMAPService    {

    @Override
    public List<MapSheetPayload> findAll() {
        QMAPSheet qmapSheet = QMAPSheet.getInstance();
        return qmapSheet.getAllSheets();
    }
}
