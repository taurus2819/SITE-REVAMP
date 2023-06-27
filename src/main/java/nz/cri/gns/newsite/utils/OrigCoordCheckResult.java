/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;

/**
 *
 * @author scaddenp
 */
public class OrigCoordCheckResult {
    private String message;
    private Point2D.Double lnglat;
    
    public OrigCoordCheckResult() {
        super();
    }
    
    public OrigCoordCheckResult(String message, Point2D.Double lnglat) {
        this.message = message;
        this.lnglat = lnglat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Point2D getLnglat() {
        return lnglat;
    }

    public void setLnglat(Point2D.Double lnglat) {
        this.lnglat = lnglat;
    }
    
    
    
}
