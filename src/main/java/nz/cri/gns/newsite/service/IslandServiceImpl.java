package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.Island;
import nz.cri.gns.newsite.repository.IslandRepository;
import nz.cri.gns.newsite.repository.NewSiteRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IslandServiceImpl implements IslandService    {

    @Autowired
    IslandRepository islandRepository;
    
    @Override
    public List<Island> findByLocation(Point location) {
        return islandRepository.findByLocation(location.getX(), location.getY());
    }
    
}
