package nz.cri.gns.newsite.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.geom.Point2D;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import nz.cri.gns.newsite.utils.OrigCoord;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * This class is facade on the SiteModel used for convenience of client for POST and PUT
 * @author scaddenp
 */
@Getter @Setter
public class SiteModelInput {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final SiteModel siteModel;
    
    private int epsg;
    private String gridref;
    private Double easting;
    private Double northing;
    private String latitude;
    private String longitude;
    private String format;
    private String auditMsg;

    public SiteModelInput() {
        this.siteModel = new SiteModel();
    }

    public SiteModelInput(String siteName, Integer methodId, Double accuracy, String Directions, 
               Double height, Integer heightMethodId, Double heightAccuracy, String countyCode, String comment, Integer ownerId,
               int epsg, String gridref, Double easting, Double northing, String latitude, String longitude, String format, String auditMsg) {
        this.siteModel = new SiteModel();
        this.siteModel.setSiteName(siteName);
        this.siteModel.setMethodId(methodId);
        this.siteModel.setAccuracy(accuracy);
        this.siteModel.setDirections(Directions);
        this.siteModel.setHeight(height);
        this.siteModel.setHeightMethodId(heightMethodId);
        this.siteModel.setHeightAccuracy(heightAccuracy);
        this.siteModel.setCountryCode(countyCode);
        this.siteModel.setComment(comment);
        this.siteModel.setOwnerId(ownerId);
        this.epsg = epsg;
        this.gridref = gridref;
        this.easting = easting;
        this.northing = northing;
        this.latitude = latitude;
        this.longitude = longitude;
        this.format = format;
        this.auditMsg = auditMsg;
    }
    
    
    public String getSiteName() {
        return siteModel.getSiteName();
    }

    public void setSiteName(String siteName) {
        this.siteModel.setSiteName(siteName);
    }

    public Integer getMethodId() {
        return siteModel.getMethodId();
    }

    public void setMethodId(Integer methodId) {
        this.siteModel.setMethodId(methodId);
    }

    public Double getAccuracy() {
        return siteModel.getAccuracy();
    }

    public void setAccuracy(Double accuracy) {
        this.siteModel.setAccuracy(accuracy);
    }

    public String getDirections() {
        return siteModel.getDirections();
    }

    public void setDirections(String directions) {
        this.siteModel.setDirections(directions);
    }
    
    public Double getHeight() {
        return siteModel.getHeight();
    }

    public void setHeight(Double height) {
        this.siteModel.setHeight(height);
    }

    public Integer getHeightMethodId() {
        return siteModel.getHeightMethodId();
    }

    public void setHeightMethodId(Integer heightMethodId) {
        this.siteModel.setHeightMethodId(heightMethodId);
    }

    public Double getHeightAccuracy() {
        return siteModel.getHeightAccuracy();
    }

    public void setHeightAccuracy(Double heightAccuracy) {
        this.siteModel.setHeightAccuracy(heightAccuracy);
    }

    public String getCountryCode() {
        return siteModel.getCountryCode();
    }

    public void setCountryCode(String countryCode) {
        this.siteModel.setCountryCode(countryCode);
    }

    public String getComment() {
        return siteModel.getComment();
    }

    public void setComment(String comment) {
        this.siteModel.setComment(comment);
    }

    public Integer getOwnerId(){
        return this.siteModel.getOwnerId();
    }
    
    public void setOwnerId(Integer OwnerId){
        this.siteModel.setOwnerId(OwnerId);
    }
    
   
/**
 * Perform all necessary transformations on the SiteModelInput object to emit a valid SiteModel
 * This involves creating the OrigCoord structure, and converting input coordinates into
 * WGS84 lat/long for the SiteModel.
 * @return
 * @throws InvalidLatLonFormat
 * @throws InvalidOrigCoordinate
 * @throws FactoryException
 * @throws MismatchedDimensionException
 * @throws TransformException 
     * @throws com.fasterxml.jackson.core.JsonProcessingException 
 */    
    public SiteModel toSiteModel() throws InvalidLatLonFormat,InvalidOrigCoordinate, FactoryException, MismatchedDimensionException, TransformException, JsonProcessingException{
        siteModel.setOrigCoord(OrigCoord.createOrigFormatJson(epsg,format,gridref,latitude,longitude, easting, northing));
        Point2D inputPt = new Point2D.Double();
        Point2D lnglat;
        format = format.toUpperCase();
        if (format.equals("EN")) {
            inputPt.setLocation(easting,northing);
            lnglat = OrigCoord.toWGS84(epsg, inputPt);
         //   lnglat = OrigCoord.parseLatLng(Double.toString(lnglat.getX()), Double.toString(lnglat.getY()));
        } else if (format.startsWith("D")) {
            lnglat = OrigCoord.parseLatLng(latitude, longitude);
            if (epsg!=4326) {
                inputPt.setLocation(lnglat.getX(),lnglat.getY());
                lnglat = OrigCoord.toWGS84(epsg, inputPt);
            }
        } else if (format.equals("GRIDREF")) {
            // deal with grid ref
            inputPt = OrigCoord.parseGridRef(epsg,gridref);
            lnglat = OrigCoord.toWGS84(epsg, inputPt);
            //lnglat = OrigCoord.parseLatLng(Double.toString(lnglat.getX()), Double.toString(lnglat.getY()));
        } else {
            throw new InvalidOrigCoordinate("Not a valid format");
        }
        if (!siteModel.getCountryCode().equals("NZ") && OrigCoord.isNZCode(epsg)) {
            throw new InvalidOrigCoordinate("NZ coordinate system used for a foreign locality");
        }
        
        siteModel.setLat(lnglat.getY());
        double lon = lnglat.getX();
        siteModel.setLon(lon);
        siteModel.setAuditMsg(getAuditMsg());
        siteModel.setOwnerId(getOwnerId());
        siteModel.setOrigSystemId(OrigCoord.getOrigSystemId(epsg, format));
        return siteModel;
    }
}
