/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.model;

import com.fasterxml.jackson.databind.JsonNode;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import nz.cri.gns.newsite.utils.OrigCoord;
import org.locationtech.proj4j.ProjCoordinate;

/**
 *
 * @author scaddenp
 */
public class SiteModelInput {
    private SiteModel siteModel;
    private int epsg;
    private String gridref;
    private Double easting;
    private Double northing;
    private String latitude;
    private String longitude;
    private String format;

    public SiteModelInput() {
        this.siteModel = new SiteModel();
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

    public int getEpsg() {
        return epsg;
    }

    public void setEpsg(int epsg) {
        this.epsg = epsg;
    }

    public String getGridref() {
        return gridref;
    }

    public void setGridref(String gridref) {
        this.gridref = gridref;
    }

    public Double getEasting() {
        return easting;
    }

    public void setEasting(Double easting) {
        this.easting = easting;
    }

    public Double getNorthing() {
        return northing;
    }

    public void setNorthing(Double northing) {
        this.northing = northing;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    public SiteModel toSiteModel() throws InvalidLatLonFormat,InvalidOrigCoordinate{
        siteModel.setOrigCoord(OrigCoord.createOrigFormatJson(epsg,format,gridref,latitude,longitude, easting, northing));
        ProjCoordinate inputPt = new ProjCoordinate();
        ProjCoordinate latlng;
        if (format.equals("EN")) {
            inputPt.x = easting;
            inputPt.y = northing;
            latlng = OrigCoord.toWGS84(epsg, inputPt);
        } else if (format.startsWith("D")) {
            latlng = OrigCoord.parseLatLng(latitude, longitude);
        } else {
            // deal with grid ref
            latlng = null;
        }
        siteModel.setLat(latlng.y);
        siteModel.setLon(latlng.x);
        return siteModel;
    }
}
