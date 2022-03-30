/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorenh
 */
public class CoordinateConverter {
    
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
        bounds.setSRID(4326);
        return bounds;
    }
    
    public static Geometry convertGeometryCoordinates(Geometry geometry, int epsg) throws TransformException, FactoryException {
        
        List<Coordinate> convertedCoordinates = new ArrayList<>();
        Coordinate targetCoord;
        for(Coordinate srcCoord : geometry.getCoordinates())   {
            Point2D latlng = OrigCoord.MakeLatLongPt(srcCoord.x, srcCoord.y, epsg);
            targetCoord = new Coordinate(latlng.getY(), latlng.getX());
            convertedCoordinates.add(targetCoord);
        }
        
        GeometryFactory gf = new GeometryFactory();
        Polygon bounds = gf.createPolygon((Coordinate[])convertedCoordinates.toArray(new Coordinate[0]));
        bounds.setSRID(4326);
        return bounds;
    }
}
