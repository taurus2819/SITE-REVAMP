package nz.cri.gns.newsite.service;

import java.util.List;
import nz.cri.gns.newsite.exception.ResourceMissingException;
import nz.cri.gns.newsite.model.DatumMethod;
import nz.cri.gns.newsite.repository.MethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *
 * @author sorenh
 */
@Service
@Scope("singleton")
@Configurable
public class MethodServiceImpl implements MethodService    {

    @Autowired
    MethodRepository methodRepository;
    
    @Override
    public DatumMethod find(Integer id) {
        return methodRepository.findById(id).orElseThrow(() -> new ResourceMissingException(String.format("Method with id %d not found", id)));
    }
    
    @Override
    public List<DatumMethod> findByName(String methodName) {
        List<DatumMethod> result = methodRepository.findByName(methodName);
        if(result.isEmpty())    {
           throw new ResourceMissingException(String.format("Method with name %s not found", methodName));
        }
        return methodRepository.findByName(methodName);
    }

    @Override
    public List<DatumMethod> findAll() {
        return methodRepository.findAll(sortByNameAsc());
    }
    
    private Sort sortByNameAsc()    {
        return Sort.by(Sort.Direction.ASC, "name");
    }
    
}
