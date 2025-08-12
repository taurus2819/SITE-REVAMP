package nz.cri.gns.newsite.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "orig_system", schema = "sc")
//@Getter
public class OrigSysId implements Serializable {
    @Column(name = "system_id", updatable = false)
    @NotNull
    @Id
    private Integer systemId;
    @Column(name = "system_code")
    @NotNull
    private String systemCode;
    @Column(name = "coord_system")
    @NotNull
    private String coordSystem;
    @Column(name = "type")
    @NotNull
    private String type;
    @Column(name = "allowed_unit")
    @NotNull
    private String allowedUnit;
    @Column(name = "human_name")
    @NotNull
    private String humanName;

    public @NotNull Integer getSystemId() {
        return systemId;
    }

    public @NotNull String getSystemCode() {
        return systemCode;
    }

    public @NotNull String getCoordSystem() {
        return coordSystem;
    }

    public @NotNull String getType() {
        return type;
    }

    public @NotNull String getAllowedUnit() {
        return allowedUnit;
    }

    public @NotNull String getHumanName() {
        return humanName;
    }
}
