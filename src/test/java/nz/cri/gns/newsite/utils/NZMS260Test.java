/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;

import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class NZMS260Test {
    
    @Test
    public void testGetFullCoordinates() {
        Point2D outputPt = NZMS260.getInstance().getFullCoordinates(27200, "U20", 9670, 9780);
        assertEquals(outputPt.getX(), 2796700, 0.001);
        assertEquals(outputPt.getY(), 6197800, 0.001);
        outputPt = NZMS260.getInstance().getFullCoordinates(27200, "D49", 2325, 2591);
        assertEquals(outputPt.getX(), 2123250.000, 0.001);
        assertEquals(outputPt.getY(), 5325910.000, 0.001);
        InvalidOrigCoordinate iocException = assertThrows(InvalidOrigCoordinate.class, ()->{
            NZMS260.getInstance().getFullCoordinates(27200, "Z50", 2325, 2591);
        });
        assertEquals("Not within the valid mapsheet list for epsg: 27200", iocException.getMessage());
    }
    
}
