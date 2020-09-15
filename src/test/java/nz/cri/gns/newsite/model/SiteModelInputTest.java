/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class SiteModelInputTest {
    
    @Test
    public void testToSiteModel() throws Exception {
        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere", 
               null, null, 3.5, "NZ", "testing",1618,
               27200, "U20/967978", null, null, null, null,"GridRef", "Unit test");
        SiteModel sm = smi.toSiteModel();
        assertEquals(sm.getLat(), -39.37637937, 0.0001);
        assertEquals(sm.getLon(), 176.32814751, 0.0001);
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":27200, \"format\":\"gridRef\", \"gridReference\":\"U20/967978\"}"));        
    }
    
//    @Test
//    public void testToSiteModel2() throws Exception {
//        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere", 
//               null, null, 3.5, "NZ", "testing",
//               27200, "U20/967978", null, null, null, null,"blah");
//        SiteModel sm = smi.toSiteModel();
//        assertEquals(sm.getLat(), -39.37637937, 0.0001);
//        assertEquals(sm.getLon(), 176.32814751, 0.0001);
//        ObjectMapper mapper = new ObjectMapper();
//        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":27200, \"format\":\"gridRef\", \"gridReference\":\"U20/967978\"}"));        
//    }
    
}
