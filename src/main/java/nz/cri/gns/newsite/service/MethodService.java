package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.model.DatumMethod;

/**
 *
 * @author sorenh
 */
public interface MethodService {
    public DatumMethod find(Integer id);
    public List<DatumMethod> findByName(String methodName);
    public List<DatumMethod> findAll();
}
