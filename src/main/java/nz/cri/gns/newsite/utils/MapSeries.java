package nz.cri.gns.newsite.utils;

import org.locationtech.jts.geom.Geometry;

/**
 *
 * @author sorenh
 */
public interface MapSeries {
    
    public static int getDefaultEPSG() {
        throw new UnsupportedOperationException("Not supported yet."); 
    };

    public static Geometry getBoundingBox(String mapsheet){
        throw new UnsupportedOperationException("Not supported yet."); 
    };
    
    public String lookupMapSheet(double easting, double northing); 

}
