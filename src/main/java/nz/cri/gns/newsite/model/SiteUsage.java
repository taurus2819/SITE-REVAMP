package nz.cri.gns.newsite.model;

/**
 *
 * @author sitikond
 */
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "site_useage", schema = "sc")

public class SiteUsage implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Column(name = "su_id")
    @NotNull
    @Id
    @SequenceGenerator(name = "sc.site_useage_su_id_seq", sequenceName = "sc.site_useage_su_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc.site_useage_su_id_seq")
    private Integer su_id; 

    @Column(name = "site_id")
    private Integer siteId; 

    @Column(name = "used_by")
    private String usedBy;
    public SiteUsage() {

    }

    public SiteUsage(Integer siteId, String usedBy ){ 
        super();
        this.siteId = siteId;
        this.usedBy = usedBy;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public Integer getSu_id() {
        return su_id;
    }
    
    @Override
    public String toString(){
        return new ToStringCreator(this)
                .append("siteId", this.getSiteId())
                .append("used_by", this.getUsedBy())
                .toString();
    }
}
