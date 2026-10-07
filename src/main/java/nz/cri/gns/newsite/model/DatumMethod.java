package nz.cri.gns.newsite.model;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;;

/**
 *
 * @author sorenh
 */
@Entity
@Table(name = "method", schema = "sc")
//@TypeDefs({
//    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
//})
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
