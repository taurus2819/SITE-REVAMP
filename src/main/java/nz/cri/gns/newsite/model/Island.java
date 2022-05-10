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
@Table(name = "island", schema = "sc")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
@Getter
public class Island implements Serializable {
    
    @Column(name = "island_id", updatable = false)
    @NotNull
    @Id
    private Integer islandId;

    @Column(name = "name")
    private String name;
    
    @Column(name = "country_code")
    private String countryCode;
    
    @Column(name = "top")
    private double bboxTop;
    
    @Column(name = "bottom")
    private double bboxBottom;
    
    @Column(name = "left")
    private double bboxLeft;
    
    @Column(name = "right")
    private double bboxRight;

}
