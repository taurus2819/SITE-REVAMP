/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.internal.parser.JSONParser;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import org.gbif.common.parsers.core.OccurrenceParseResult;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.common.parsers.geospatial.CoordinateParseUtils;
import org.gbif.common.parsers.geospatial.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

/**
 *
 * @author scaddenp
 */
public class OrigCoord {
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
    
    //data from oracle
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
    
    public static ProjCoordinate parseLatLng(String latitude, String longitude) {
      OccurrenceParseResult<LatLng> ll = CoordinateParseUtils.parseLatLng(latitude,longitude);
      if (ll.getConfidence() != ParseResult.CONFIDENCE.DEFINITE && ll.getConfidence() != ParseResult.CONFIDENCE.PROBABLE) {
          throw new InvalidLatLonFormat("Invalid lat/lon format" + ll.getConfidence().toString());
      }
      ProjCoordinate latlng = new ProjCoordinate(ll.getPayload().getLng(),ll.getPayload().getLat());
      return latlng;
    }
    
    public static ProjCoordinate toWGS84(int epsg, ProjCoordinate inputPt) {
       CRSFactory crsf = new CRSFactory();
       CoordinateTransformFactory ctf = new CoordinateTransformFactory();
       CoordinateReferenceSystem epsg4326 = crsf.createFromName("EPSG:4326");
       CoordinateReferenceSystem crs = crsf.createFromName(String.format("EPSG:%04d", epsg));
       CoordinateTransform toEPSG4326 = ctf.createTransform(crs,epsg4326);           
       ProjCoordinate outputPt = new ProjCoordinate();
       toEPSG4326.transform(inputPt, outputPt);
       return outputPt;
       
    }
    
    public static JsonNode createOrigFormatJson(int epsg, String format, String gridRef, String latitude, String longitude, double easting, double northing) {
      return null; //TODO    
    }
}
