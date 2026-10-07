package nz.cri.gns.newsite.model;

/**
 *
 * @author sitikond
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nz.cri.gns.newsite.audits.AuditLog;

import org.springframework.core.style.ToStringCreator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import org.locationtech.jts.geom.Geometry;




@Entity
@Table(name = "site", schema = "sc")
//@TypeDefs({
//    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
//})
@NoArgsConstructor
public class SiteModel implements Site{
    
    private static final long serialVersionUID = 1L;
    
    public static final int SITE_EPSG = 4326;

    @Column(name = "site_id", updatable = false)
    @NotNull
    @Id
    @SequenceGenerator(name = "sc.site_proposed_site_id_seq", sequenceName = "sc.site_proposed_site_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc.site_proposed_site_id_seq")
    @Getter
    private Integer siteId;  //51955

    @Column(name = "site_name")
    @Getter @Setter
    private String siteName;  //GNS Physical Location Site, Dunedin

    @Column(name = "latitude")
    @Getter @Setter
    private Double lat;		//-45.864369921

    @Column(name = "longitude")
    @Getter @Setter
    private Double lon;		//170.513135754

    @Column(name = "method_id")
    @Getter @Setter
    private Integer methodId;	//3

    @Column(name = "accuracy")
    @Getter @Setter
    private Double accuracy;		//10

    @Column(name = "directions")
    @Getter @Setter
    private String directions;	//764 Cumberland Street, Dunedin

    @Column(name = "orig_system_id")
    @Getter @Setter
    private Integer origSystemId; 	//38

    /*
    Scenario: GD49 latlong in DMS Given OrigCoords of: "origCoords":{"epsg":4272,"format":"DMS","longitude":"176 34' 23.01E","latitude":"41 02' 42.51S" } 
    Scenario: WGS84 latlong in DD Given OrigCoords of: "origCoords":{"epsg":4326,"format":"DD","longitude":"172.44","latitude":"-45.5675" }
    Scenario: NZTM full coordinates Given OrigCoords of: "origCoords":{"epsg":2193,"format":"EN","easting":"1528677.3","northing":"5413457.7" } 
    Scenario: NZMG grid reference Given OrigCoords of: "origCoords":{"epsg":27200,"format":"gridref","gridReference":"U20/962872" } 
    */
    //@Type(type = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "orig_coord", columnDefinition = "json")
    @Getter @Setter
    private JsonNode origCoord;	
    
    @Column(name = "height")
    @Getter @Setter
    private Double height;		//44

    @Column(name = "h_method_id")
    @Getter @Setter
    private Integer heightMethodId;		//3

    @Column(name = "h_accuracy")
    @Getter @Setter
    private Double heightAccuracy;	//10

    @Column(name = "country_code")
    @Getter @Setter
    private String countryCode;		//NZ

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="country_code", insertable = false, updatable = false)
    //@Getter @Setter
    @JsonIgnore
    private Country country;

    @Column(name = "flag")
    @Getter @Setter
    private Integer flag;				//null

    @Column(name = "comment")
    @Getter @Setter
    private String comment;			//"blah blah"

    @JsonIgnore
    @Column(name = "shape", columnDefinition = "Geometry")
    @Getter
    private Geometry shape;
    
    @Column(name="owner_id")
    @JsonIgnore
    @Getter @Setter
    private Integer ownerId;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, targetEntity=AuditLog.class, orphanRemoval = true)      //mappedBy = "sitemodel",
    @JoinColumn(name = "site_id")
    @Getter @Setter
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    @OneToMany(targetEntity=SiteUsage.class, mappedBy="siteId", fetch=FetchType.LAZY)
    @JsonIgnore
    private List<SiteUsage> users = new ArrayList<>();
   
    @JsonIgnore
    @Transient
    @Getter @Setter
    private String auditMsg;

    @JsonIgnore
    @Transient
    @Getter @Setter
    private String clientUser = "NA";
    
    public SiteModel(String siteName, double lat, double lon, Integer methodId, Double accuracy,
            String directions, Integer origSystemId, JsonNode origCoord, Double height, Integer heightMethodId,
            Double heightAccuracy, String countryCode, Integer flag, String comment, Integer ownerId, String auditMsg, String clientUser ){  //   /*, String shape*/, String auditlogInfoMsg) {
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
        this.clientUser = clientUser;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="orig_system_id", insertable = false, updatable = false)

    private OrigSysId origSysId;

    public String getOrigSysIdTitle(){
        return Optional.ofNullable(origSysId)
                .map(OrigSysId::getHumanName)
                .orElse(null);
    }
    
    /**
     * Moved here from application (PET): "Lat/Long:" : "Grid Ref"
     * @return a human-friendly category of the original spatial reference system category
     */
    public String getOrigSystemCategoryTitle()  {
        if(origCoord == null || origCoord.get("format") == null)    {
            return null;
        }
        switch(origCoord.get("format").asText())   {
            case "DD":
                return "Lat/Long:";
            case "EN":
                return "Easting/Northing:";
            case "gridref":
                return "Grid Ref.:";
            default:
                return "Coordinates:";
        }
        /*if(origSystemId.equals(29) || origSystemId.equals(38))  {   //geographic
            return "Lat/Long:";
        } else  {
            return "Grid Ref:";
        }*/
    }
    
    public String getPrettyOrigSystemCoordinates()  {
        return Optional.ofNullable(getOrigCoord())
                .map(JsonNode::toPrettyString)
                .orElse(null);
    }

    public void setShape(Geometry shape) {
        if (shape != null) {
            shape.setSRID(SITE_EPSG);
        }
        this.shape = shape;
    }
    
    public boolean addAuditLog(AuditLog auditLogInfo){
//        auditLogInfo.setSiteModel(this);
        return getAuditLogs().add(auditLogInfo);
    }
    
    public void removeAuditLogs(){
        
    }

    public List<String> getUsers() {
        List<String> usedby = new ArrayList<>();
        users.forEach((u) -> {
            usedby.add(u.getUsedBy());
        });
        return usedby;
    }

    public String getCountryName()    {
        if(country != null) {
            return country.getCountryName();
        }
        return null;
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
                .append("User", this.getClientUser())
                .toString();
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
        final SiteModel other = (SiteModel) obj;
        return Objects.equals(this.siteId, other.siteId);
    }

    public void addSiteUsage(SiteUsage siteUser) {
        users.add(siteUser);
    }

    public void setSiteId(int i) {
        this.siteId = i;
    }
}