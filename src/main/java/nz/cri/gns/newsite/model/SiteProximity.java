/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author scaddenp This is facade for siteModel, plus distance from a target
 * point used in isCloseTo api for returning the value of potential conflict
 * points.
 *
 */
public class SiteProximity implements Serializable,Comparable {

    private double proximity;
    private Integer siteId;
    private String siteName;
    private Double accuracy;
    private Double height;
    private String directions;
    private String comment;
    private Double latitude;
    private Double longitude;
    private String origCoord;
    private  List<String> users;

    public SiteProximity() {
        super();
    }

    public SiteProximity(SiteModel siteModel, double proximity) {
        this.proximity = proximity;
        this.siteId = siteModel.getSiteId();
        this.siteName = siteModel.getSiteName();
        this.accuracy = siteModel.getAccuracy();
        this.height = siteModel.getHeight();
        this.directions = siteModel.getDirections();
        this.comment = siteModel.getComment();
        this.latitude = siteModel.getLat();
        this.longitude = siteModel.getLon();
        this.origCoord = siteModel.getOrigCoord().toString();
        this.users = siteModel.getUsers();        
    }

    public double getProximity() {
        return proximity;
    }

    public void setProximity(double proximity) {
        this.proximity = proximity;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteid) {
        this.siteId = siteid;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getOrigCoords() {
        return origCoord;
    }

    public void setOrigCoords(String origCoords) {
        this.origCoord = origCoords;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public int compareTo(Object sp) {
        double dist = ((SiteProximity)sp).getProximity();
        return (int) ( this.proximity - dist);
    }

    
}
