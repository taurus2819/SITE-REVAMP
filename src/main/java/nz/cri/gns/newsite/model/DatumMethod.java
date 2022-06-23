package nz.cri.gns.newsite.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 *
 * @author sorenh
 */
@Entity
@Table(name = "method", schema = "sc")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
@Getter
public class DatumMethod implements Serializable {
    
    @Column(name = "method_id", updatable = false)
    @NotNull
    @Id
    private Integer methodId;

    @Column(name = "method")
    private String name;
    
    @Column(name = "nom_accuracy_xy")
    private final Float xyAcc = -1F;
    
    @Column(name = "nom_accuracy_z")
    private final Float zAcc = -1F;
  
}
