package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Converts provided geometries to target EPSG 4326 (WGS84 Lat/Lon)
 * @author sorenh
 */
public class CoordinateConverter {
    
    public static final int STANDARD_EPSG_4326 = 4326;
    
    public static Polygon convertBbox(double minNorth, double minEast, double maxNorth, double maxEast, int epsg) throws TransformException, FactoryException {
        Point2D latlng1 = OrigCoord.MakeLatLongPt(minEast, maxNorth, epsg);
        Point2D latlng2 = OrigCoord.MakeLatLongPt(maxEast, maxNorth, epsg);
        Point2D latlng3 = OrigCoord.MakeLatLongPt(maxEast, minNorth, epsg);
        Point2D latlng4 = OrigCoord.MakeLatLongPt(minEast, minNorth, epsg);
        Coordinate[] sc = new Coordinate[5];
        sc[0] = new Coordinate(latlng1.getY(), latlng1.getX());
        sc[1] = new Coordinate(latlng2.getY(), latlng2.getX());
        sc[2] = new Coordinate(latlng3.getY(), latlng3.getX());
        sc[3] = new Coordinate(latlng4.getY(), latlng4.getX());
        sc[4] = new Coordinate(latlng1.getY(), latlng1.getX());
        GeometryFactory gf = new GeometryFactory();
        Polygon bounds = gf.createPolygon(sc);
        bounds.setSRID(STANDARD_EPSG_4326);
        return bounds;
    }
    
    public static Geometry convertGeometryCoordinates(Geometry geometry, int fromEpsg) throws TransformException, FactoryException {
        return convertGeometryCoordinates(geometry, fromEpsg, STANDARD_EPSG_4326);
    }

    
    public static Geometry convertGeometryCoordinates(Geometry geometry, int fromEpsg, int toEpsg) throws TransformException, FactoryException {
        
        List<Coordinate> convertedCoordinates = new ArrayList<>();
        Coordinate targetCoord;
        for(Coordinate srcCoord : geometry.getCoordinates())   {
            Point2D latlng = OrigCoord.MakePt(srcCoord.x, srcCoord.y, fromEpsg, toEpsg);
            targetCoord = new Coordinate(latlng.getX(), latlng.getY());
            convertedCoordinates.add(targetCoord);
        }
        
        if(geometry instanceof Polygon) {
            return createPolygon(convertedCoordinates, toEpsg);
        } else {    //Point as default
            return createPoint(convertedCoordinates, toEpsg);
        }
    }
    
    private static Geometry createPolygon(List<Coordinate> coordinates, int toEpsg)    {
        GeometryFactory gf = new GeometryFactory();
        Polygon polygon = gf.createPolygon((Coordinate[])coordinates.toArray(new Coordinate[0]));
        polygon.setSRID(toEpsg);
        return polygon;
    }
    
    private static Geometry createPoint(List<Coordinate> coordinates, int toEpsg)    {
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(coordinates.get(0));
        point.setSRID(toEpsg);
        return point;
    }
    
}
