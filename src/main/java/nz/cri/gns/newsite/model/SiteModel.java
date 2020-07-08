package nz.cri.gns.newsite.model;

/**
 *
 * @author sitikond
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.core.style.ToStringCreator;

@Entity
@Table(name = "site_proposed", schema = "sc")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
public class SiteModel {

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

    @Type(type = "json")
    @Column(name = "orig_coord", columnDefinition = "json")
    private JsonNode origCoord;	//"["A29","007","500"]"
    
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

//    @JsonIgnore
//    @Column(name = "shape")
//    private String shape;
    
    public SiteModel() {

    }

    public SiteModel(String siteName, double lat, double lon, Integer methodId, Double accuracy,
            String directions, Integer origSystemId, JsonNode origCoord, Double height, Integer heightMethodId,
            Double heightAccuracy, String countryCode, Integer flag, String comment /*, String shape*/) {
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
//        if(origCoord.startsWith("{")){
//            //treat this as a JSONObject
//            String json = "...";
//            JSONObject obj = new JSONObject(json);
//        }else if(origCoord.startsWith("[")){        
//            JSONArray arr = new JSONArray(origCoord);
//            this.origCoord = arr.toString();
//        }        
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

//    public String getShape() {
//        return shape;
//    }
//
//    public void setShape(String shape) {
//        this.shape = shape;
//    }
    
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
