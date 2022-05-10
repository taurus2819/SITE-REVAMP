package nz.cri.gns.newsite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import nz.cri.gns.newsite.model.Island;
import org.springframework.data.jpa.repository.Query;

public interface IslandRepository extends JpaRepository<Island, Integer>   { 
       
    @Query(value = "Select i from #{#entityName} i where i.bboxLeft < :x and i.bboxRight > :x and i.bboxBottom < :y and i.bboxTop > :y")
    public List<Island> findByLocation(double x, double y);
    
    @Query(value = "Select i from #{#entityName} i where i.name = :name")
    public List<Island> findByName(String name);
   
}
