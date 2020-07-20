/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

/**
 *
 * @author sitikond
 */
public class ConversionToWgs84 {
        private double lat;
        private double lon;
        private double convertedLat;
        private double convertedLon;
               
        public ConversionToWgs84(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
            doConversionLlToWgs84();
        }

        public double getConvertedLat() {
            return convertedLat;
        }

        public double getConvertedLon() {
            return convertedLon;
        }
        
        private void doConversionLlToWgs84(){
        }
    
}
