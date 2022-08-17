package nz.cri.gns.newsite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.DatumMethod;
import org.springframework.data.jpa.repository.Query;

public interface MethodRepository extends JpaRepository<DatumMethod, Integer>   { 

    
    @Query(value = "Select i from #{#entityName} i where i.name = :name")
    public List<DatumMethod> findByName(String name);
   
}
