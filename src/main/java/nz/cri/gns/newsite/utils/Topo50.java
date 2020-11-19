/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;

/**
 * Utility class for converting NZTopo50 (NZ Transverse Mercator, NZTM, EPSG 2193)  gridrefs into full Easting northing
 *
 * @author scaddenp
 */
public class Topo50 extends MapSheet{

    static final String NZTMSL = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    static final String validMapReference = "ABC";
    
    private static boolean isValidMapSheet(char mapSheetReferenceChar) {
        return (validMapReference.indexOf(mapSheetReferenceChar) == -1);
    }

    /**
     * Function to convert topo50 truncated coordinates derived from a grid reference into full coordinates
     * @param epsg  2193
     * @param mapsheet The mapsheet reference. eg BE33, CF04
     * @param truncEast the 4-figure truncated easting value (3 figure truncated eastings should be multiplied by 10 before passing to this routine)
     * @param truncNorth the 4-figure truncated northing value (3 figure truncated northings should be multiplied by 10 before passing to this routine)
     * @return a Point2D x,y containing the full easting and northings
     */

    public static Point2D getFullCoordinates(int epsg, String mapsheet, int truncEast, int truncNorth) {
        char s1 = mapsheet.charAt(0);
        if(isValidMapSheet(s1))
        {
            throw new InvalidOrigCoordinate("Invalid mapsheet reference letter for this epsg: " + epsg);
        }
        int sheet;
        try {
            sheet = Integer.parseInt(mapsheet.substring(2));
        } catch (Exception e ) {
            throw new InvalidOrigCoordinate("invalid sheet no. for this epsg: " + epsg);            
        }
        int nid = NZTMSL.indexOf(mapsheet.charAt(1)) +1;
        if (sheet < 4 || sheet > 45 || nid < 1 || nid > 24) {
            throw new InvalidOrigCoordinate("grid reference is outside the bounds of this epsg: " + epsg);
        }
        int southS;
        switch (s1) {
            case 'A':
                southS = 6810000;
                break;
            case 'B':
                southS = 5946000;
                break;
            case 'C':
                southS = 5082000;
                break;
            default:
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of this epsg: " + epsg);
        }
        southS = southS - nid * 36000;
        int southST = Math.floorDiv(southS, 100000) * 100000;
        double north = truncNorth * 10.0d + southST;
        int eastS = sheet * 24000 + 988000;
        int eastST = Math.floorDiv(eastS, 100000) * 100000;
        double east = truncEast * 10 + eastST;
        return new Point2D.Double(east, north);
    }
}
