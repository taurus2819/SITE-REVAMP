/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class NZMS1Test {
    
    @Test
    public void testGetFullCoordinates() {
        Point2D outputPt = NZMS1.getInstance().getFullCoordinates(27291, "N108", 5789, 9883);
        assertEquals(outputPt.getX(), 157890, 0.001);
        assertEquals(outputPt.getY(), 398830, 0.001);
        outputPt = NZMS1.getInstance().getFullCoordinates(27291, "N1", 1510, 4940);
        assertEquals(outputPt.getX(), 15100, 0.001);
        assertEquals(outputPt.getY(), 949400, 0.001);
        outputPt = NZMS1.getInstance().getFullCoordinates(27292, "S33", 3340, 6400);
        assertEquals(outputPt.getX(), 633400, 0.001);
        assertEquals(outputPt.getY(), 764000, 0.001);
    }
    
}
