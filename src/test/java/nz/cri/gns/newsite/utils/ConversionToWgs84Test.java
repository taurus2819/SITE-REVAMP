/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.data.Offset;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
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
  @Test
    public void testConversionToWgs84() {
        double lat = -38.94870612;
        double lon = 174.2910453;
        Point2D latlng;
        try {
            latlng = OrigCoord.MakeLatLongPt(lon, lat, 4272);
            System.out.println("Latitude = " + latlng.getY());
            System.out.println("Longitude = " + latlng.getX());
            assertThat(latlng.getY()).isCloseTo(-38.94694413269838, Offset.offset(0.00001));
            assertThat(latlng.getX()).isCloseTo(174.29124453616492, Offset.offset(0.00001));
        } catch (TransformException | FactoryException | MismatchedDimensionException ex) {
            Logger.getLogger(ConversionToWgs84Test.class.getName()).log(Level.SEVERE, null, ex);
        }
//        Assert.assertEquals((Double)-43.69834663393171, (Double)llToWgs84.getConvertedLon());
        
//        Assert.assertEquals((Double)-43.69834663393171, (Double)llToWgs84.getConvertedLon()); 
    }  
}
