package nz.cri.gns.newsite.model;

import lombok.Getter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "country", schema = "sc")
@Getter
public class Country  implements Serializable {

    @Column(name = "country_code", updatable = false)
    @NotNull
    @Id
    private String countryCode;

    @Column(name = "country_name")
    @NotNull
    private String countryName;

    @Column(name = "country_dial_code")
    @NotNull
    private Integer countryDialCode;

}
