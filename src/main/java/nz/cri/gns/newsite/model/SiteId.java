package nz.cri.gns.newsite.model;

/**
 *
 * @author sorenh
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Objects;
import jakarta.persistence.*;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "site_proposed", schema = "sc")
//@TypeDefs({
//    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
//})
@Getter
public class SiteId implements Site{
    
    private static final long serialVersionUID = 1L;
    
    public static final int SITE_EPSG = 4326;

    @Column(name = "site_id", updatable = false)
    @NotNull
    @Id
    @SequenceGenerator(name = "sc.site_proposed_site_id_seq", sequenceName = "sc.site_proposed_site_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc.site_proposed_site_id_seq")
    private Integer siteId;  //51955
    
    @JsonIgnore
    @Column(name = "shape", columnDefinition = "Geometry")
    private Geometry shape;
    
    @JsonIgnore
    @Getter @Setter
    @Column(name = "country_code")
    private String countryCode;
    
    public SiteId() {}
    
    public void setShape(Geometry shape) {
        shape.setSRID(4326);
        this.shape = shape;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.siteId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SiteId other = (SiteId) obj;
        return Objects.equals(this.siteId, other.siteId);
    }
    
    
}
