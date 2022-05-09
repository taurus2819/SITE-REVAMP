/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class Topo50Test {
    
    @Test
    public void testGetFullCoordinates() {
        Point2D outputPt = Topo50.getInstance().getFullCoordinates(2193, "CC13", 151, 805);
        assertEquals(outputPt.getX(), 1301510, 0.001);
        assertEquals(outputPt.getY(), 4908050, 0.001);
    }    
    
    
    @Test
    public void testBbox()  {
        
        double tolerance = 0.001;
        Geometry bbox = Topo50.getInstance().getBoundingBox("BD32");
        
        assertTrue(bbox instanceof Polygon);
        
        Coordinate[] coordinates = bbox.getCoordinates();
        assertTrue(coordinates.length == 5);
        
        assertEquals(1756000, coordinates[0].x, tolerance);
        assertEquals(5802000, coordinates[0].y, tolerance);
    }
}
