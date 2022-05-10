package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.MapSheetPayload;

/**
 *
 * @author sorenh
 */
public interface QMAPService {
    public List<MapSheetPayload> findAll();
}
