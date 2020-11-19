/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import javax.validation.constraints.AssertTrue;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class Topo50Test {
    
    @Test
    public void testGetFullCoordinates() {
        Point2D outputPt = Topo50.getFullCoordinates(2193, "CC13", 151, 805);
//        System.out.println("X = " + outputPt.getX());
//        System.out.println("Y = " + outputPt.getY());
        assertEquals(outputPt.getX(), 1301510, 0.001);
        assertEquals(outputPt.getY(), 4908050, 0.001);
    }    
}
