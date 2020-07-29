/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;

/**
 * Abstract class for map sheet cutups.
 * @author scaddenp
 */
abstract class MapSheet {
    /**
     * Function to convert mapsheet truncated coordinates derived from a grid reference into full coordinates
     * @param mapsheet The mapsheet reference. eg U34
     * @param truncEast the 4-figure truncated easting value (3 figure truncated eastings should be multiplied by 10 before passing to this routine)
     * @param truncNorth the 4-figure truncated northing value (3 figure truncated northings should be multiplied by 10 before passing to this routine)
     * @return a Point2D x,y containing the full easting and northings
     */
    static Point2D getFullCoordinates(String mapsheet, int truncEast, int truncNorth) {
        return null;
    };
    
}
