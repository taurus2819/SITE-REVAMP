/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.model;

/**
 *
 * @author scaddenp
 * This is facade for siteModel, plus distance from a target point used in
 * isCloseTo api for returning the value of potential conflict points.
 * 
 */
public class SiteProximity {

    private final SiteModel siteModel;
    private final double proximity;

    public SiteProximity(SiteModel siteModel, double proximity) {
        this.siteModel = siteModel;
        this.proximity = proximity;
    }
    
    public long getSiteId() {
        return siteModel.getSiteId();
    }

    public double getProximity() {
        return proximity;
    }

    public String getSiteName() {
        return siteModel.getSiteName();
    }

    public Double getAccuracy() {
        return siteModel.getAccuracy();
    }

    public String getDirections() {
        return siteModel.getDirections();
    }

    public Double getHeight() {
        return siteModel.getHeight();
    }

    public String getComment() {
        return siteModel.getComment();
    }

    public double getLatitude() {
        return siteModel.getLat();
    }

    public double getLongitude() {
        return siteModel.getLon();
    }

}
