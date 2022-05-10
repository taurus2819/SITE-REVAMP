package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.Island;
import org.locationtech.jts.geom.Point;

/**
 *
 * @author sorenh
 */
public interface IslandService {
    public List<Island> findByLocation(Point location);
    public List<Island> getAll();
}
