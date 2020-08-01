/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class ConversionToWgs84Test {
    
//    @Test
    public void testConversionFromLlToWgs84() {
        double lat=-43.7;
        double lon=174.73;
        ConversionToWgs84 llToWgs84 = new ConversionToWgs84(lat, lon);
        System.out.println("Latitude = " + llToWgs84.getConvertedLat());
        System.out.println("Longitude = " + llToWgs84.getConvertedLon());
        Assert.assertEquals((Double)174.7301531230962, (Double)llToWgs84.getConvertedLat()); 
//        Assert.assertEquals((Double)-43.69834663393171, (Double)llToWgs84.getConvertedLon()); 
    }
    
}
