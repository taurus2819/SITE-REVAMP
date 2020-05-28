/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class OrigCoordTest {
    
    @Test
    public void testGetJsonString() {
        assert (OrigCoord.getJsonString(75, "2696700|5953800") ==null);
        System.out.println(OrigCoord.getJsonString(38, "2696700|5953800"));
        System.out.println(OrigCoord.getJsonString(16, "D50|9610|1630"));        
        System.out.println(OrigCoord.getJsonString(16, "C40|8612|5642"));        
        System.out.println(OrigCoord.getJsonString(28, "-39.08102037777|174.52428614166"));        
        assert (OrigCoord.getJsonString(38, "2696700|5953800").equals("{\"epsg\":27200, \"format\":\"EN\", \"easting\":2696700, \"northing\":5953800}"));
        assert (OrigCoord.getJsonString(16, "D50|9610|1630").equals("{\"epsg\":27200, \"format\":\"gridref\", \"gridReference\":\"D50/961163\"}"));        
        assert (OrigCoord.getJsonString(16, "C40|8612|5642").equals("{\"epsg\":27200, \"format\":\"gridref\", \"gridReference\":\"C40/86125642\"}"));        
        assert (OrigCoord.getJsonString(28, "-39.08102037777|174.52428614166").equals("{\"epsg\":4167, \"format\":\"DD\", \"latitude\":-39.08102037777, \"longitude\":174.52428614166}"));        
    }
    
}
