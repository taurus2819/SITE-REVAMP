/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import nz.cri.gns.util.map.Datum;
import nz.cri.gns.util.map.DatumFactory;
import nz.cri.gns.util.map.WGS84;

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
            Datum.LatLong ll = new Datum.LatLong(lat, lon);
            WGS84 wgs84 = (WGS84)DatumFactory.createDatum("WGS84");
            Datum.Coordinate wgsll = wgs84.convertFromNZGD49(ll);
//            setConvertedLat(wgsll.getEastWest());
            this.convertedLat = wgsll.getEastWest();
//            setConvertedLon(wgsll.getNorthSouth());
            this.convertedLon = wgsll.getNorthSouth();
        }
    
}
