/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import nz.cri.gns.newsite.utils.ObjectMapperWrapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author sitikond
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SiteModelInputTest {
    
    @Test
    public void testToSiteModel() throws Exception {
        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere", 
               null, null, 3.5, "NZ", "testing",1618,
               27200, "U20/967978", null, null, null, null,"GridRef", "Unit test");
        SiteModel sm = smi.toSiteModel();
        assertEquals(sm.getLat(), -39.37637937, 0.0001);
        assertEquals(sm.getLon(), 176.32814751, 0.0001);
        int origId = sm.getOrigSystemId();
        assertEquals(origId,16);
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":27200, \"format\":\"gridRef\", \"gridReference\":\"U20/967978\"}"));        
    }

    @Test
    public void testToSiteModelLL() throws Exception {
        SiteModelInput smi = new SiteModelInput("test1",3,null,"somewhere", 
               null, null, 3.5, "NZ", "testing",1618,
               4326, null, null, null, "-39.16630691","173.1451122" ,"DD", "Unit test");
        SiteModel sm = smi.toSiteModel();
        assertEquals(sm.getLat(), -39.16630691, 0.0001);
        assertEquals(sm.getLon(), 173.1451122, 0.0001);
        int origId = sm.getOrigSystemId();
        assertEquals(origId,73);
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":4326,\"format\":\"DD\",\"longitude\":\"173.1451122\",\"latitude\":\"-39.16630691\"}"));        
    }
    
    @Test
    public void testToSiteModelLLGD49() throws Exception {
        SiteModelInput smi = new SiteModelInput("test1",3,null,"somewhere", 
               null, null, 3.5, "NZ", "testing",1618,
               4272, null, null, null, "-39.0","173.0" ,"DD", "Unit test");
        SiteModel sm = smi.toSiteModel();
        assertEquals(sm.getLat(), -38.99823, 0.0001);
        assertEquals(sm.getLon(), 173.00020, 0.0001);
        int origId = sm.getOrigSystemId();
        assertEquals(origId,29);
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":4272,\"format\":\"DD\",\"longitude\":\"173.0\",\"latitude\":\"-39.0\"}"));        
    }
    
    @Test
    public void testToSiteModelEN() throws Exception {
        SiteModelInput smi = new SiteModelInput("test1",3,null,"somewhere", 
               null, null, 3.5, "NZ", "testing",1618,
               27200, null, 2920547.0, 6270637.0, null,null ,"EN", "Unit test");
        SiteModel sm = smi.toSiteModel();
        assertEquals(sm.getLat(), -38.67077204, 0.0001);
        assertEquals(sm.getLon(), 177.71914878, 0.0001);
        int origId = sm.getOrigSystemId();
        assertEquals(origId,38);
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":27200,\"format\":\"EN\",\"easting\":2920547.0,\"northing\":6270637.0}"));        
    }
    
    @Test
    public void TestWrongCountrythrowsException() {
        SiteModelInput smi = new SiteModelInput("test1",3,null,"somewhere", 
               null, null, 3.5, "US", "testing",1618,
               27200, null, 2920547.0, 6270637.0, null,null ,"EN", "Unit test");
        Exception exception = assertThrows(InvalidOrigCoordinate.class, ()-> {
            SiteModel sm = smi.toSiteModel();            
        });
        assertTrue(exception.getMessage().contains("NZ coordinate system used for a foreign locality"));        
    }
//    @Test
//    public void testToSiteModel2() throws Exception {
//        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere", 
//               null, null, 3.5, "NZ", "testing",
//               27200, "U20/967978", null, null, null, null,"blah");
//        SiteModel sm = smi.toSiteModel();
//        assertEquals(sm.getLat(), -39.37637937, 0.0001);
//        assertEquals(sm.getLon(), 176.32814751, 0.0001);
//        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();

//        assertEquals(sm.getOrigCoord(),mapper.readTree("{\"epsg\":27200, \"format\":\"gridRef\", \"gridReference\":\"U20/967978\"}"));        
//    }
    
}
