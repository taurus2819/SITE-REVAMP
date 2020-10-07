package nz.cri.gns.newsite.model;

/**
 *
 * @author sitikond
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import nz.cri.gns.newsite.audits.AuditLog;
import org.gbif.common.shaded.com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "site_proposed", schema = "sc")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
public class SiteModel implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Column(name = "site_id", updatable = false)
    @NotNull
    @Id
    @SequenceGenerator(name = "sc.site_proposed_site_id_seq", sequenceName = "sc.site_proposed_site_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc.site_proposed_site_id_seq")
    private Integer siteId;  //51955

    @Column(name = "site_name")
    private String siteName;  //GNS Physical Location Site, Dunedin

    @Column(name = "latitude")
    @NotNull
    private double lat;		//-45.864369921

    @Column(name = "longitude")
    @NotNull
    private double lon;		//170.513135754

    @Column(name = "method_id")
    private Integer methodId;	//3

    @Column(name = "accuracy")
    private Double accuracy;		//10

    @Column(name = "directions")
    private String directions;	//764 Cumberland Street, Dunedin

    @Column(name = "orig_system_id")
    private Integer origSystemId; 	//38

    /*
    Scenario: GD49 latlong in DMS Given OrigCoords of: "origCoords":{"epsg":4272,"format":"DMS","longitude":"176 34' 23.01E","latitude":"41 02' 42.51S" } 
    Scenario: WGS84 latlong in DD Given OrigCoords of: "origCoords":{"epsg":4326,"format":"DD","longitude":"172.44","latitude":"-45.5675" }
    Scenario: NZTM full coordinates Given OrigCoords of: "origCoords":{"epsg":2193,"format":"EN","easting":"1528677.3","northing":"5413457.7" } 
    Scenario: NZMG grid reference Given OrigCoords of: "origCoords":{"epsg":27200,"format":"gridref","gridReference":"U20/962872" } 
    */
    @Type(type = "json")
    @Column(name = "orig_coord", columnDefinition = "json")
    private JsonNode origCoord;	
    
    @Column(name = "height")
    private Double height;		//44

    @Column(name = "h_method_id")
    private Integer heightMethodId;		//3

    @Column(name = "h_accuracy")
    private Double heightAccuracy;	//10

    @Column(name = "country_code")
    private String countryCode;		//NZ

    @Column(name = "flag")
    private Integer flag;				//null

    @Column(name = "comment")
    private String comment;			//"blah blah"

    @JsonIgnore
    @Column(name = "shape", columnDefinition = "Geometry")
    private Geometry shape;
    
    @Column(name="owner_id")
    @JsonIgnore
    private Integer ownerId;
    
    @OneToMany(cascade = CascadeType.ALL, targetEntity=AuditLog.class, orphanRemoval = true)      //mappedBy = "sitemodel",
    @JoinColumn(name = "site_id")
    private List<AuditLog> auditLogs = new ArrayList<>();
   
    @JsonIgnore
    @Transient
    private String auditMsg;
    
    public SiteModel() {

    }

    public SiteModel(String siteName, double lat, double lon, Integer methodId, Double accuracy,
            String directions, Integer origSystemId, JsonNode origCoord, Double height, Integer heightMethodId,
            Double heightAccuracy, String countryCode, Integer flag, String comment, Integer ownerId, String auditMsg ){  //   /*, String shape*/, String auditlogInfoMsg) {
        super();
        this.siteName = siteName;
        this.lat = lat;
        this.lon = lon;
        this.methodId = methodId;
        this.accuracy = accuracy;
        this.directions = directions;
        this.origSystemId = origSystemId;
        this.origCoord = origCoord;
        this.height = height;
        this.heightMethodId = heightMethodId;
        this.heightAccuracy = heightAccuracy;
        this.countryCode = countryCode;
        this.flag = flag;
        this.comment = comment;
//        this.shape = shape;
        this.ownerId = ownerId;
        this.auditMsg = auditMsg;
    }

    public Integer getSiteId() {
        return this.siteId;
    }    

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Integer getOrigSystemId() {
        return origSystemId;
    }

    public void setOrigSystemId(Integer origSystemId) {
        this.origSystemId = origSystemId;
    }

    public JsonNode getOrigCoord() {
        return origCoord;
    }

    public void setOrigCoord(JsonNode origCoord) {
        this.origCoord = origCoord;
    }
    
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getHeightMethodId() {
        return heightMethodId;
    }

    public void setHeightMethodId(Integer heightMethodId) {
        this.heightMethodId = heightMethodId;
    }

    public Double getHeightAccuracy() {
        return heightAccuracy;
    }

    public void setHeightAccuracy(Double heightAccuracy) {
        this.heightAccuracy = heightAccuracy;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Geometry getShape() {        
        return shape;
    }

    public void setShape(Geometry shape) {
        shape.setSRID(4326);
        this.shape = shape;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }   

    public String getAuditMsg() {
        return auditMsg;
    }

    public void setAuditMsg(String auditMsg) {
        this.auditMsg = auditMsg;
    }
    
    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }
    
    public boolean addAuditLog(AuditLog auditLogInfo){
//        auditLogInfo.setSiteModel(this);
        return getAuditLogs().add(auditLogInfo);
    }
    
    public void removeAuditLogs(){
        
    }
    
    @Override
    public String toString(){
//        JsonNode jsonnode = this.getOrigCoord();
//        System.out.println("JsonNode length= " + jsonnode.size());
//         System.out.println("JsonNode length= " + jsonnode.getNodeType());
//         System.out.println("JsonNode length= " + jsonnode.get(1));
        return new ToStringCreator(this)
                .append("id", this.getSiteId())
                .append("site_name", this.getSiteName())
                .append("lat", this.getLat())
                .append("lon", this.getLon())
                .append("OrigSysId", this.getOrigSystemId())
                .append("OrigCoord", this.getOrigCoord())
//                .append("Shape", this.getShape())
                .toString();
    }
}
