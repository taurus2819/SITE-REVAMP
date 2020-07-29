/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.gbif.common.parsers.core.OccurrenceParseResult;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.common.parsers.geospatial.CoordinateParseUtils;
import org.gbif.common.parsers.geospatial.LatLng;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import static org.geotools.referencing.CRS.AxisOrder.EAST_NORTH;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Class for handling of original coordinate formats
 * @author scaddenp
 */
public class OrigCoord {

    /**
     * Parse a general grid reference into full easting northing values in the given epsg.
     * @param epsg gridref projection to work with. Currently supported are NZ260 (27200), NZTopo50 (2193) and NZ national yard (27291 and 27292)
     * @param gridref a gridrefernce eg N112/349211 or CE45 34567812 or D20/817456. It can be 6 or 8 figure easting. Needs a single character, non-numeric delimiter between map sheet and grid ref
     * @return A point2D containing the full easting (x) and northing (y).
     * @throws InvalidOrigCoordinate 
     */
    public static Point2D parseGridRef(int epsg, String gridref) throws InvalidOrigCoordinate{
        // poor mans parser
        String sheetCode="";
        String ref="";
        int j=0;
        Point2D outputPt;
        gridref = gridref.trim();
        for (int i=0; i<3; i++) {
            if (Character.isDigit(gridref.charAt(i))) {                
                j = i;
                break;
            }
        }
        if (j==0) {
            throw new InvalidOrigCoordinate("No map sheet on grid reference");
        }
        for (int k=j; k<j+4; k++) {
            if (!Character.isDigit(gridref.charAt(k))) {
               sheetCode = gridref.substring(0,k);
               ref = gridref.substring(k+1);
               break;
            }
        }
        if (ref.isEmpty()) {
            throw new InvalidOrigCoordinate("No delimited between mapsheet and ref in gridref");
        }
        j = ref.length();
        int east, north;
        try {
        if (j==6) {
            east = Integer.parseInt(ref.substring(0,3))*10;
            north = Integer.parseInt(ref.substring(3))*10;
        } else if (j==8) {
            east = Integer.parseInt(ref.substring(0,4));
            north = Integer.parseInt(ref.substring(4));            
        } else {
            throw new InvalidOrigCoordinate("No delimited between mapsheet and ref in gridref");            
        }
        } catch ( Exception e) {
            throw new InvalidOrigCoordinate("Non-numeric grid ref");            
        }
        if (epsg == 27292 || epsg == 27291) {
            outputPt = NZMS1.getFullCoordinates(sheetCode, east, north);            
        } else {
            throw new InvalidOrigCoordinate("gridref epsg is not supported yet");                        
        }
        return outputPt;
    }
    
    public static class OrigCoordDetail {
        int epsg;
        String format;      
        private OrigCoordDetail(int epsg, String format) {
            this.epsg = epsg;
            this.format = format;
        }
      }
  
    private static final Map<Integer, OrigCoordDetail> ORIG_COORD_LIST = createMap();

    private static Map<Integer, OrigCoordDetail> createMap() {
        Map<Integer,OrigCoordDetail> result = new HashMap<>();
        result.put(1,	new OrigCoordDetail(27219,"EN"));
        result.put(2,	new OrigCoordDetail(2119,"EN"));
        result.put(3,	new OrigCoordDetail(27232,"EN"));
        result.put(4,	new OrigCoordDetail(2132,"EN"));
        result.put(5,	new OrigCoordDetail(27217,"EN"));
        result.put(6,	new OrigCoordDetail(2117,"EN"));
        result.put(7,	new OrigCoordDetail(3793,"EN"));
        result.put(8,	new OrigCoordDetail(27214,"EN"));
        result.put(9,	new OrigCoordDetail(2114,"EN"));
        result.put(10,	new OrigCoordDetail(27105,"EN"));
        result.put(11,	new OrigCoordDetail(2105,"EN"));
        result.put(12,	new OrigCoordDetail(27225,"EN"));
        result.put(13,	new OrigCoordDetail(2125,"EN"));
        result.put(14,	new OrigCoordDetail(27218,"EN"));
        result.put(15,	new OrigCoordDetail(2118,"EN"));
        result.put(16,	new OrigCoordDetail(27200,"gridref"));
        result.put(17,	new OrigCoordDetail(27292,"gridref"));
        result.put(18,	new OrigCoordDetail(27208,"EN"));
        result.put(19,	new OrigCoordDetail(2108,"EN"));
        result.put(20,	new OrigCoordDetail(27221,"EN"));
        result.put(21,	new OrigCoordDetail(2121,"EN"));
        result.put(22,	new OrigCoordDetail(27223,"EN"));
        result.put(23,	new OrigCoordDetail(2123,"EN"));
        result.put(24,	new OrigCoordDetail(27216,"EN"));
        result.put(25,	new OrigCoordDetail(2116,"EN"));
        result.put(26,	new OrigCoordDetail(27227,"EN"));
        result.put(27,	new OrigCoordDetail(2127,"EN"));
        result.put(28,	new OrigCoordDetail(4167,"DD"));
        result.put(29,	new OrigCoordDetail(4272,"DD"));
        result.put(30,	new OrigCoordDetail(4672,"DD"));
        result.put(31,	new OrigCoordDetail(27220,"EN"));
        result.put(32,	new OrigCoordDetail(2120,"EN"));
        result.put(33,	new OrigCoordDetail(27292,"EN"));
        result.put(34,	new OrigCoordDetail(27215,"EN"));
        result.put(35,	new OrigCoordDetail(2115,"EN"));
        result.put(36,	new OrigCoordDetail(27228,"EN"));
        result.put(37,	new OrigCoordDetail(2128,"EN"));
        result.put(38,	new OrigCoordDetail(27200,"EN"));
        result.put(39,	new OrigCoordDetail(27230,"EN"));
        result.put(40,	new OrigCoordDetail(2130,"EN"));
        result.put(41,	new OrigCoordDetail(27222,"EN"));
        result.put(42,	new OrigCoordDetail(2122,"EN"));
        result.put(44,	new OrigCoordDetail(27224,"EN"));
        result.put(45,	new OrigCoordDetail(2124,"EN"));
        result.put(46,	new OrigCoordDetail(27206,"EN"));
        result.put(47,	new OrigCoordDetail(2106,"EN"));
        result.put(48,	new OrigCoordDetail(27207,"EN"));
        result.put(49,	new OrigCoordDetail(2107,"EN"));
        result.put(50,	new OrigCoordDetail(27231,"EN"));
        result.put(51,	new OrigCoordDetail(2131,"EN"));
        result.put(52,	new OrigCoordDetail(27209,"EN"));
        result.put(53,	new OrigCoordDetail(2109,"EN"));
        result.put(54,	new OrigCoordDetail(27226,"EN"));
        result.put(55,	new OrigCoordDetail(2126,"EN"));
        result.put(56,	new OrigCoordDetail(27210,"EN"));
        result.put(57,	new OrigCoordDetail(2110,"EN"));
        result.put(58,	new OrigCoordDetail(27212,"EN"));
        result.put(59,	new OrigCoordDetail(2112,"EN"));
        result.put(60,	new OrigCoordDetail(27211,"EN"));
        result.put(61,	new OrigCoordDetail(2111,"EN"));
        result.put(62,	new OrigCoordDetail(27213,"EN"));
        result.put(63,	new OrigCoordDetail(2113,"EN"));
        result.put(64,	new OrigCoordDetail(27229,"EN"));
        result.put(65,	new OrigCoordDetail(2129,"EN"));
        result.put(67,	new OrigCoordDetail(3788,"EN"));
        result.put(68,	new OrigCoordDetail(3793,"EN"));
        result.put(69,	new OrigCoordDetail(27291,"gridref"));
        result.put(70,	new OrigCoordDetail(27291,"EN"));
        result.put(73,	new OrigCoordDetail(4326,"DD"));
        result.put(71,	new OrigCoordDetail(2193,"EN"));
        result.put(72,	new OrigCoordDetail(2193,"gridref"));
        result.put(74,	new OrigCoordDetail(2998,"EN"));
        return Collections.unmodifiableMap(result);
    }
    
    /**
     * Convert a systemid and origcoord from the old site database in the new orig coordinate format
     * @param system_id The id from SC.ORIG_COORD used the original coordinate. It is an index to a projection.
     * @param origCoord Origincal coordinate in the string format of the original SC.SITE table
     * @return 
     */
    public static String getJsonString(int system_id, String origCoord ) {
        if (ORIG_COORD_LIST.containsKey(system_id)) {
          OrigCoordDetail ocd = ORIG_COORD_LIST.get(system_id);
          String parts[] = origCoord.split("\\|");
          String js = "{\"epsg\":" + ocd.epsg  + ", \"format\":\"" + ocd.format + "\", " ;
          switch (ocd.format) {
              case "DD":
                  if (parts.length!=2) {
                      return null;
                  }
                  js += "\"latitude\":\"" + parts[0] + "\", \"longitude\":\"" + parts[1] + "\"";
                  break;
              case "gridref":
                  if (parts.length!=3) {
                      return null;
                  }
                  js += "\"gridReference\":\"" + parts[0] + "/";
                  if (parts[1].endsWith("0") && parts[2].endsWith("0")) {
                     js += parts[1].substring(0,3) + parts[2].substring(0,3);
                  } else {
                     js += parts[1]+ parts[2];
                  }
                  js += "\"";
                  break;
              case "EN":
                  if (parts.length!=2) {
                      return null;
                  }
                  js += "\"easting\":" + parts[0] + ", \"northing\":" + parts[1];
                  break;             
          }
          js += "}";
          return js;
        } else {
            return null;
        }
    }    
    
    //data received from postgres
    public static String getEpsgInfoJsonString(int system_id, JsonNode origCoord ) {
        Gson gson = new Gson();
        if (ORIG_COORD_LIST.containsKey(system_id)) {
          OrigCoordDetail ocd = ORIG_COORD_LIST.get(system_id);
          String js = "{\"epsg\":" + ocd.epsg  + ", \"format\":\"" + ocd.format + "\", " ;
          switch (ocd.format) {
              case "DD":
                  if (origCoord.size() != 2) {
                      return null;
                  }
                  js += "\"latitude\":" + origCoord.get(0) + ", \"longitude\":" + origCoord.get(1);
                  break;
              case "gridref":
                  if (origCoord.size() != 3) {
                      return null;
                  }
                  js += "\"gridReference\":\"" + origCoord.get(0).asText() + "/";
                  if (origCoord.get(1).asText().endsWith("0") && origCoord.get(2).asText().endsWith("0")) {
                     js += origCoord.get(1).asText().substring(0,3) + origCoord.get(2).asText().substring(0,3);
                  } else {
                     js += origCoord.get(1).asText()+ origCoord.get(2).asText();
                  }
                  js += "\"";
                  break;
              case "EN":
                  if (origCoord.size()!= 2) {
                      return null;
                  }
                  js += "\"easting\":" + origCoord.get(0) + ", \"northing\":" + origCoord.get(1);
                  break;             
          }
          js += "}";
          return js;
        } else {
            return null;
        }
    }
    /**
     * Parse a character lat/long coordinate in a variety of DMS or DM formats into decimal degrees
     * @param latitude Character representation of latitude (eg 34 21' 45.23"S)
     * @param longitude Character representation of longitude ( W 175 46.4345 )
     * @return A point2D with decimal latitude (y) and longitude (x)
     */
    public static Point2D parseLatLng(String latitude, String longitude) {
      OccurrenceParseResult<LatLng> ll = CoordinateParseUtils.parseLatLng(latitude,longitude);
      if (ll.getConfidence() != ParseResult.CONFIDENCE.DEFINITE && ll.getConfidence() != ParseResult.CONFIDENCE.PROBABLE) {
          throw new InvalidLatLonFormat("Invalid lat/lon format" + ll.getConfidence().toString());
      }
      Point2D latlng = new Point2D.Double(ll.getPayload().getLng(),ll.getPayload().getLat());
      return latlng;
    }
    
    /**
     * Convert a coordinate in whatever coordinate reference system into a lat/long in WGS84
     * @param epsg The EPSG Code for the projection
     * @param inputPt easting (X) / Northing (Y) or longitude (X), Latitude (Y)
     * @return Point2D with latitude (Y) and longitude (X) in WGS84 datum
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException 
     */
    public static Point2D toWGS84(int epsg, Point2D inputPt) throws FactoryException, MismatchedDimensionException, TransformException {
       CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");
       CoordinateReferenceSystem crs = CRS.decode(String.format("EPSG:%04d", epsg));
       MathTransform transform = CRS.findMathTransform(crs,epsg4326, true);
       DirectPosition2D outputDp = new DirectPosition2D();
       DirectPosition2D inputDp;
       if (CRS.getAxisOrder(crs)== EAST_NORTH) {
           inputDp = new DirectPosition2D(crs, inputPt.getX(),inputPt.getY());
       } else {
           inputDp = new DirectPosition2D(crs, inputPt.getY(),inputPt.getX());           
       }
       transform.transform(inputDp, outputDp);
       
       return outputDp.toPoint2D();
       
    }
    
    public static JsonNode createOrigFormatJson(int epsg, String format, String gridRef, String latitude, String longitude, double easting, double northing) {
      return null; //TODO    
    }
}
