package nz.cri.gns.newsite.audits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
import jakarta.validation.constraints.NotNull;
import nz.cri.gns.newsite.model.SiteModel;
//import org.hibernate.annotations.Type;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


/**
 *
 * @author sitikond
 */
@Entity
@Table(name = "audit_log", schema = "sc")
//@TypeDefs({
//    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
//})
public class AuditLog implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @JsonIgnore
    @Column(name = "audit_log_id", updatable = false)
    @NotNull
    @Id
    @SequenceGenerator(name = "sc.audit_log_audit_log_id_seq", sequenceName = "sc.audit_log_audit_log_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc.audit_log_audit_log_id_seq")
    private Integer auditLogId;
    
    @Column(name = "site_id")
    private Integer auditSiteId;
    
    //@Type(type = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "log_info", columnDefinition = "json")
    private JsonNode logInfo;	    //example value  {"timestamp": "20200810", "loginfo":"Site updatd to new coords"}
    
    @JsonIgnore
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", insertable=false, updatable=false)
    private SiteModel siteModel;

    public AuditLog() {
    }   
    
    public AuditLog(Integer siteId, JsonNode logInfo){ //, SiteModel siteModel) {
        super();
        this.auditSiteId = siteId;
        this.logInfo = logInfo;
//        this.siteModel = siteModel;
    }

    public Integer getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Integer auditLogId) {
        this.auditLogId = auditLogId;
    }

    public Integer getAuditSiteId() {
        return auditSiteId;
    }

    public void setAuditSiteId(Integer siteId) {
        this.auditSiteId = siteId;
    }

    public JsonNode getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(JsonNode logInfo) {
        this.logInfo = logInfo;
    }

    public SiteModel getSiteModel() {
        return siteModel;
    }

    public void setSiteModel(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    @Override
    public int hashCode() {
        return this.auditLogId == null ? 0 : this.auditLogId.hashCode();
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
        final AuditLog other = (AuditLog) obj;
        if (!Objects.equals(this.auditLogId, other.auditLogId)) {
            return false;
        }
        if (!Objects.equals(this.auditSiteId, other.auditSiteId)) {
            return false;
        }
        if (!Objects.equals(this.siteModel, other.siteModel)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return "AuditLog: siteid = " + this.auditSiteId + " SiteModeID = " + this.siteModel.getSiteId() + " Log = " + this.logInfo;
    }
}
