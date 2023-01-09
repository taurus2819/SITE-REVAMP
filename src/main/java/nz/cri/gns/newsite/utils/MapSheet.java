package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Abstract class for map sheet cutups.
 *
 * @author scaddenp
 */
abstract class MapSheet {

    /**
     * Function to convert mapsheet truncated coordinates derived from a grid
     * reference into full coordinates
     *
     * @param epsg 27200(NZMG), 27292(NZGD49), 27291(NZGD49), 2193(NZGD2000)
     * @param mapsheet The mapsheet reference. eg U34
     * @param truncEast the 4-figure truncated easting value (3 figure truncated
     * eastings should be multiplied by 10 before passing to this routine)
     * @param truncNorth the 4-figure truncated northing value (3 figure
     * truncated northings should be multiplied by 10 before passing to this
     * routine)
     * @return a Point2D x,y containing the full easting and northings
     */
    public Point2D getFullCoordinates(int epsg, String mapsheet, int truncEast, int truncNorth) {
        return null;
    }
   
    public String getMapsheet(Geometry inputLocation) {
        Geometry convertedLocation;
        double easting = 0;
        double northing = 0;

        if (inputLocation.getSRID() != getMapsheetLookupEPSG()) {
            try {
                convertedLocation = CoordinateConverter.convertGeometryCoordinates(inputLocation, CoordinateConverter.STANDARD_EPSG_4326, getMapsheetLookupEPSG());
                Point p = convertedLocation.getInteriorPoint();
                Double xVal = Double.valueOf(p.getX());
                Double yVal = Double.valueOf(p.getY());
                if(xVal.toString().endsWith("E7")){    //for Antarctic long/lat values using epsg wgs84, the length of the numeric values of the double value exceeds 7 digits; and
                    easting = p.getX()/10;              //this causes some gibberish value for the mapsheet - jira AS-640
                }else{
                    easting = convertedLocation.getCoordinate().x;
                }
                if(yVal.toString().endsWith("E7")){
                    northing = p.getY()/10;
                }else{
                    northing = convertedLocation.getCoordinate().y;
                }
            } catch (TransformException | FactoryException ex) {
                Logger.getLogger(Topo50.class.getName()).log(Level.SEVERE, null, ex);
                return "Error";
            }
        } else {
            convertedLocation = inputLocation;
            easting = convertedLocation.getCoordinate().x;
            northing = convertedLocation.getCoordinate().y;
        }
        return lookupMapSheet(easting, northing);
    }

    public abstract String lookupMapSheet(double easting, double northing);
    
    public abstract int getMapsheetLookupEPSG(); 
}
