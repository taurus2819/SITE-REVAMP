package nz.cri.gns.newsite.model;

/**
 *
 * @author sitikond
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "site_proposed", schema = "sc")
public class SiteModel {

    @Column(name = "site_id")
    @NotNull
    @Id    
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
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
    
//    @JsonIgnore
//    @Column(name = "orig_coord")
//    private String origCoord;	//"2316763|5479842"

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
//    @NotNull
//    private String shape;

    public SiteModel() {

    }

    public SiteModel(String siteName, double lat, double lon, Integer methodId, Double accuracy,
            String directions, Integer origSystemId, Double height, Integer heightMethodId,
            Double heightAccuracy, String countryCode, Integer flag, String comment) {
        super();        
        this.siteName = siteName;
        this.lat = lat;
        this.lon = lon;
        this.methodId = methodId;
        this.accuracy = accuracy;
        this.directions = directions;
        this.origSystemId = origSystemId;
//        this.origCoord = origCoord;
        this.height = height;
        this.heightMethodId = heightMethodId;
        this.heightAccuracy = heightAccuracy;
        this.countryCode = countryCode;
        this.flag = flag;
        this.comment = comment;
//        this.shape = shape;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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

//    public String getOrigCoord() {
//        return origCoord;
//    }
//
//    public void setOrigCoord(String origCoord) {
//        this.origCoord = origCoord;
//    }

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

}
