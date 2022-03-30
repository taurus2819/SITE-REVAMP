/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Utility class for converting NZTopo50 (NZ Transverse Mercator, NZTM, EPSG 2193)  gridrefs into full Easting northing
 *
 * @author scaddenp
 */
public class Topo50 extends MapSheet implements MapSeries{

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
    
    /**
     * Generates the bounding geometry of the specified mapsheet in NZMG
     * @param mapsheet
     * @return 
     */
    public static Geometry getBoundingBox(String mapsheet) {
        //TODO #1 check in GIS and add test cases
        //TODO #2 handle irregular map sheet names, like 'BD39ptBE39'
        //TODO #3 check if this is actually precise enough, as current DB function has different increments
        
        char s1 = mapsheet.charAt(0);
        if(isValidMapSheet(s1))
        {
            throw new InvalidOrigCoordinate("Invalid mapsheet reference letter for mapsheet: " + mapsheet);
        }
        int sheet;
        try {
            sheet = Integer.parseInt(mapsheet.substring(2));
        } catch (NumberFormatException e ) {
            throw new InvalidOrigCoordinate("Invalid sheet no. for mapsheet: " + mapsheet);           
        }
        int nid = NZTMSL.indexOf(mapsheet.charAt(1)) +1;
        if (sheet < 4 || sheet > 45 || nid < 1 || nid > 24) {
            throw new InvalidOrigCoordinate("Grid reference is outside the bounds of this mapsheet: " + mapsheet);
        }
        int bboxSouth, bboxNorth, bboxEast, bboxWest;
        switch (s1) {
            case 'A':
                bboxSouth = 6810000;
                break;
            case 'B':
                bboxSouth = 5946000;
                break;
            case 'C':
                bboxSouth = 5082000;
                break;
            default:
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of this mapsheet: " + mapsheet);
        }
        bboxSouth = bboxSouth - nid * 36000;
        bboxNorth = bboxSouth + 36000;
        
        bboxWest = sheet * 24000 + 988000;
        bboxEast = bboxWest + 24000;

        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), getDefaultEPSG());

        Polygon polygon = factory.createPolygon(factory.createLinearRing(new Coordinate[]{
            new Coordinate(bboxWest, bboxSouth),
            new Coordinate(bboxWest, bboxNorth),
            new Coordinate(bboxEast, bboxNorth),
            new Coordinate(bboxEast, bboxSouth),
            new Coordinate(bboxWest, bboxSouth),
        }), null);   
        return polygon;
    }
    
    /**
     * NZTM
     * @return EPSG code
     */
    public static int getDefaultEPSG() {
       return 2193;
    } 
}
