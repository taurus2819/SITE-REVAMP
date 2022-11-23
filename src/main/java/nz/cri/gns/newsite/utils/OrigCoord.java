package nz.cri.gns.newsite.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import nz.cri.gns.newsite.exception.InvalidLatLonFormat;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.gbif.common.parsers.core.OccurrenceParseResult;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.common.parsers.geospatial.CoordinateParseUtils;
import org.gbif.common.parsers.geospatial.LatLng;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import static org.geotools.referencing.CRS.AxisOrder.EAST_NORTH;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Class for handling of original coordinate formats
 *
 * @author scaddenp
 */
public class OrigCoord {

    /**
     * Parse a general grid reference into full easting northing values in the
     * given epsg.
     *
     * @param epsg gridref projection to work with. Currently supported are
     * NZ260 (27200), NZTopo50 (2193) and NZ national yard (27291 and 27292)
     * @param gridref a gridrefernce eg N112/349211 or CE45 34567812 or
     * D20/817456. It can be 6 or 8 figure easting. Needs a single character,
     * non-numeric delimiter between map sheet and grid ref
     * @return A point2D containing the full easting (x) and northing (y).
     * @throws InvalidOrigCoordinate
     */
    public static Point2D parseGridRef(int epsg, String gridref) throws InvalidOrigCoordinate {
        // poor mans parser
        String sheetCode = "";
        String ref = "";
        int j = 0;
        Point2D outputPt;
        gridref = gridref.trim();
        for (int i = 0; i < 3; i++) {
            if (Character.isDigit(gridref.charAt(i))) {
                j = i;
                break;
            }
        }
        if (j == 0) {
            throw new InvalidOrigCoordinate("No map sheet on grid reference");
        }
        for (int k = j; k < j + 4; k++) {
            if (!Character.isDigit(gridref.charAt(k))) {
                sheetCode = gridref.substring(0, k);
                ref = gridref.substring(k + 1);
                break;
            }
        }
        if (ref.isEmpty()) {
            throw new InvalidOrigCoordinate("No delimited between mapsheet and ref in gridref");
        }
        j = ref.length();
        int east, north;
        try {
            if (j == 6) {
                east = Integer.parseInt(ref.substring(0, 3)) * 10;
                north = Integer.parseInt(ref.substring(3)) * 10;
            } else if (j == 8) {
                east = Integer.parseInt(ref.substring(0, 4));
                north = Integer.parseInt(ref.substring(4));
            } else {
                throw new InvalidOrigCoordinate("No delimited between mapsheet and ref in gridref");
            }
        } catch (Exception e) {
            throw new InvalidOrigCoordinate("Non-numeric grid ref");
        }
        switch (epsg) {
            case 27292:
            case 27291:
                outputPt = NZMS1.getInstance().getFullCoordinates(epsg, sheetCode, east, north);
                break;
            case 2193:
                outputPt = Topo50.getInstance().getFullCoordinates(epsg, sheetCode, east, north);
                break;
            case 27200:
                outputPt = NZMS260.getInstance().getFullCoordinates(epsg, sheetCode, east, north);
                break;
            default:
                throw new InvalidOrigCoordinate("gridref epsg is not supported yet");
        }
        return outputPt;
    }
    
    @Getter @Setter
    public static class OrigCoordDetail implements Serializable{
        
        int epsg;
        boolean nzOnly;
        String format;

        private OrigCoordDetail(int epsg, String format, boolean nzOnly) {
            this.epsg = epsg;
            this.format = format;
            this.nzOnly = nzOnly;
        }
        private OrigCoordDetail(int epsg, String format) {
            this.epsg = epsg;
            this.format = format;
            this.nzOnly = true;
        }
    }

    private static final Map<Integer, OrigCoordDetail> ORIG_COORD_LIST = createMap();

    private static Map<Integer, OrigCoordDetail> createMap() {
        Map<Integer, OrigCoordDetail> result = new HashMap<>();
        result.put(1, new OrigCoordDetail(27219, "EN"));
        result.put(2, new OrigCoordDetail(2119, "EN"));
        result.put(3, new OrigCoordDetail(27232, "EN"));
        result.put(4, new OrigCoordDetail(2132, "EN"));
        result.put(5, new OrigCoordDetail(27217, "EN"));
        result.put(6, new OrigCoordDetail(2117, "EN"));
        result.put(7, new OrigCoordDetail(5519, "EN"));
        result.put(8, new OrigCoordDetail(27214, "EN"));
        result.put(9, new OrigCoordDetail(2114, "EN"));
        result.put(10, new OrigCoordDetail(27105, "EN"));
        result.put(11, new OrigCoordDetail(2105, "EN"));
        result.put(12, new OrigCoordDetail(27225, "EN"));
        result.put(13, new OrigCoordDetail(2125, "EN"));
        result.put(14, new OrigCoordDetail(27218, "EN"));
        result.put(15, new OrigCoordDetail(2118, "EN"));
        result.put(16, new OrigCoordDetail(27200, "gridref"));
        result.put(17, new OrigCoordDetail(27292, "gridref"));
        result.put(18, new OrigCoordDetail(27208, "EN"));
        result.put(19, new OrigCoordDetail(2108, "EN"));
        result.put(20, new OrigCoordDetail(27221, "EN"));
        result.put(21, new OrigCoordDetail(2121, "EN"));
        result.put(22, new OrigCoordDetail(27223, "EN"));
        result.put(23, new OrigCoordDetail(2123, "EN"));
        result.put(24, new OrigCoordDetail(27216, "EN"));
        result.put(25, new OrigCoordDetail(2116, "EN"));
        result.put(26, new OrigCoordDetail(27227, "EN"));
        result.put(27, new OrigCoordDetail(2127, "EN"));
        result.put(28, new OrigCoordDetail(4167, "DD"));
        result.put(29, new OrigCoordDetail(4272, "DD"));
        result.put(30, new OrigCoordDetail(4673, "DD"));
        result.put(31, new OrigCoordDetail(27220, "EN"));
        result.put(32, new OrigCoordDetail(2120, "EN"));
        result.put(33, new OrigCoordDetail(27292, "EN"));
        result.put(34, new OrigCoordDetail(27215, "EN"));
        result.put(35, new OrigCoordDetail(2115, "EN"));
        result.put(36, new OrigCoordDetail(27228, "EN"));
        result.put(37, new OrigCoordDetail(2128, "EN"));
        result.put(38, new OrigCoordDetail(27200, "EN"));
        result.put(39, new OrigCoordDetail(27230, "EN"));
        result.put(40, new OrigCoordDetail(2130, "EN"));
        result.put(41, new OrigCoordDetail(27222, "EN"));
        result.put(42, new OrigCoordDetail(2122, "EN"));
        result.put(44, new OrigCoordDetail(27224, "EN"));
        result.put(45, new OrigCoordDetail(2124, "EN"));
        result.put(46, new OrigCoordDetail(27206, "EN"));
        result.put(47, new OrigCoordDetail(2106, "EN"));
        result.put(48, new OrigCoordDetail(27207, "EN"));
        result.put(49, new OrigCoordDetail(2107, "EN"));
        result.put(50, new OrigCoordDetail(27231, "EN"));
        result.put(51, new OrigCoordDetail(2131, "EN"));
        result.put(52, new OrigCoordDetail(27209, "EN"));
        result.put(53, new OrigCoordDetail(2109, "EN"));
        result.put(54, new OrigCoordDetail(27226, "EN"));
        result.put(55, new OrigCoordDetail(2126, "EN"));
        result.put(56, new OrigCoordDetail(27210, "EN"));
        result.put(57, new OrigCoordDetail(2110, "EN"));
        result.put(58, new OrigCoordDetail(27212, "EN"));
        result.put(59, new OrigCoordDetail(2112, "EN"));
        result.put(60, new OrigCoordDetail(27211, "EN"));
        result.put(61, new OrigCoordDetail(2111, "EN"));
        result.put(62, new OrigCoordDetail(27213, "EN"));
        result.put(63, new OrigCoordDetail(2113, "EN"));
        result.put(64, new OrigCoordDetail(27229, "EN"));
        result.put(65, new OrigCoordDetail(2129, "EN"));
        result.put(67, new OrigCoordDetail(210001, "EN"));
        result.put(68, new OrigCoordDetail(32359, "EN"));
        result.put(69, new OrigCoordDetail(27291, "gridref"));
        result.put(70, new OrigCoordDetail(27291, "EN"));
        result.put(73, new OrigCoordDetail(4326, "DD",false));
        result.put(71, new OrigCoordDetail(2193, "EN"));
        result.put(72, new OrigCoordDetail(2193, "gridref"));
        result.put(74, new OrigCoordDetail(2998, "EN",false));
        result.put(77, new OrigCoordDetail(3788, "EN",false));
        result.put(78, new OrigCoordDetail(3789, "EN",false));
        result.put(79, new OrigCoordDetail(3793, "EN",false));
        return Collections.unmodifiableMap(result);
    }
    
    
    public static OrigCoordDetail getOrigCoordDetails(int legacyId)  {
        return ORIG_COORD_LIST.get(legacyId);
    }
    
    public static boolean isNZCode(int epsg) {
      for (Map.Entry<Integer, OrigCoordDetail> entry : ORIG_COORD_LIST.entrySet()) {
            // Check if value matches with given value
            if (entry.getValue().epsg == epsg) {
                return entry.getValue().nzOnly;
            }
      }
      return false;
    }
    /**
     * Attempt to match the epsg and format to an old OrigSystemId code
     * @param epsg - EPGS code to search for
     * @param format - Format type to search for (matching format in ORIG_COORD_LIST
     * @return matching system id if found, otherwise null
     */
    
    public static Integer getOrigSystemId(int epsg, String format) {
      if (format.equalsIgnoreCase("DMS")) {
          format = "DD";
      }
      for (Map.Entry<Integer, OrigCoordDetail> entry : ORIG_COORD_LIST.entrySet()) {
            // Check if value matches with given value
            if (entry.getValue().epsg == epsg && entry.getValue().format.equalsIgnoreCase(format)) {
                return entry.getKey();
            }
      }        
      return null;
    }

    /**
     * Convert a systemid and origcoord from the old site database in the new
     * orig coordinate format
     *
     * @param system_id The id from SC.ORIG_COORD used the original coordinate.
     * It is an index to a projection.
     * @param origCoord Origincal coordinate in the string format of the
     * original SC.SITE table
     * @return
     */
    public static String getJsonString(int system_id, String origCoord) {
        if (ORIG_COORD_LIST.containsKey(system_id)) {
            OrigCoordDetail ocd = ORIG_COORD_LIST.get(system_id);
            String parts[] = origCoord.split("\\|");
            String js = "{\"epsg\":" + ocd.epsg + ", \"format\":\"" + ocd.format + "\", ";
            switch (ocd.format) {
                case "DD":
                    if (parts.length != 2) {
                        return null;
                    }
                    js += "\"latitude\":\"" + parts[0] + "\", \"longitude\":\"" + parts[1] + "\"";
                    break;
                case "gridref":
                    if (parts.length != 3) {
                        return null;
                    }
                    js += "\"gridReference\":\"" + parts[0] + "/";
                    if (parts[1].endsWith("0") && parts[2].endsWith("0")) {
                        js += parts[1].substring(0, 3) + parts[2].substring(0, 3);
                    } else {
                        js += parts[1] + parts[2];
                    }
                    js += "\"";
                    break;
                case "EN":
                    if (parts.length != 2) {
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
    public static String getEpsgInfoJsonString(int system_id, JsonNode origCoord) {
        Gson gson = new Gson();
        if (ORIG_COORD_LIST.containsKey(system_id)) {
            OrigCoordDetail ocd = ORIG_COORD_LIST.get(system_id);
            String js = "{\"epsg\":" + ocd.epsg + ", \"format\":\"" + ocd.format + "\", ";
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
                        js += origCoord.get(1).asText().substring(0, 3) + origCoord.get(2).asText().substring(0, 3);
                    } else {
                        js += origCoord.get(1).asText() + origCoord.get(2).asText();
                    }
                    js += "\"";
                    break;
                case "EN":
                    if (origCoord.size() != 2) {
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
     * Parse a character lat/long coordinate in a variety of DMS or DM formats
     * into decimal degrees
     *
     * @param latitude Character representation of latitude (eg 34 21' 45.23"S)
     * @param longitude Character representation of longitude ( W 175 46.4345 )
     * @return A point2D with decimal latitude (y) and longitude (x)
     */
    public static Point2D parseLatLng(String latitude, String longitude) {
        OccurrenceParseResult<LatLng> ll = CoordinateParseUtils.parseLatLng(latitude, longitude);
        if (ll.getConfidence() != ParseResult.CONFIDENCE.DEFINITE && ll.getConfidence() != ParseResult.CONFIDENCE.PROBABLE) {
            throw new InvalidLatLonFormat("Invalid lat/lon format" + ll.getConfidence().toString());
        }
        Point2D latlng = new Point2D.Double(ll.getPayload().getLng(), ll.getPayload().getLat());
        return latlng;
    }

    /**
     * Convert an east/north pair in a given epsg code to a point2D containing
     * 4326 lat/long coordinates
     *
     * @param easting
     * @param northing
     * @param epsg
     * @return Point2D where X = longitude and Y = latitude in WGS84 (4326)
     * @throws TransformException
     * @throws FactoryException
     * @throws MismatchedDimensionException
     */
    public static Point2D MakeLatLongPt(double easting, double northing, int epsg) throws TransformException, FactoryException, MismatchedDimensionException {
        Point2D latlng;
        Point2D inputPt = new Point2D.Double();
        inputPt.setLocation(easting, northing);
        latlng = OrigCoord.toWGS84(epsg, inputPt);
        return latlng;
    }
    
    public static Point2D MakePt(double easting, double northing, int inputEpsg, int outputEpsg) throws TransformException, FactoryException, MismatchedDimensionException {
        Point2D latlng;
        Point2D inputPt = new Point2D.Double();
        inputPt.setLocation(easting, northing);
        latlng = OrigCoord.convertEpsg(inputEpsg, outputEpsg, inputPt);
        return latlng;
    }

    /**
     * Convert an east/north pair in a given epsg code to a Geometry Point
     * containing 4326 lat/long coordinates
     *
     * @param easting
     * @param northing
     * @param epsg
     * @return Point geometry representation of input
     * @throws TransformException
     * @throws FactoryException
     */
    public static Point MakeGeomPt(double easting, double northing, int epsg) throws TransformException, FactoryException {
        Point2D latlng = MakeLatLongPt(easting, northing, epsg);
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(latlng.getX(), latlng.getY()));
        point.setSRID(4326);
        return point;
    }

    /**
     * Convert a coordinate in whatever coordinate reference system into a
     * lat/long in WGS84
     *
     * @param epsg The EPSG Code for the projection
     * @param inputPt easting (X) / Northing (Y) or longitude (X), Latitude (Y)
     * @return Point2D with latitude (Y) and longitude (X) in WGS84 datum
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    public static Point2D toWGS84(int epsg, Point2D inputPt) throws FactoryException, MismatchedDimensionException, TransformException {
        return convertEpsg(epsg, 4326, inputPt);
    }
    
    public static Point2D convertEpsg(int inputEpsg, int outputEpsg, Point2D inputPt) throws FactoryException, MismatchedDimensionException, TransformException {

        CoordinateReferenceSystem inputCrs = CRS.decode(String.format("EPSG:%04d", inputEpsg, true));  //always EAST_NORTH / X_Y
        CoordinateReferenceSystem outputCrs = CRS.decode(String.format("EPSG:%04d", outputEpsg, true)); //always EAST_NORTH / X_Y

        MathTransform transform = CRS.findMathTransform(inputCrs, outputCrs, true);
        DirectPosition2D outputDp = new DirectPosition2D();
        DirectPosition2D inputDp;
        
        if (CRS.getAxisOrder(inputCrs) == EAST_NORTH) {
            inputDp = new DirectPosition2D(inputCrs, inputPt.getX(), inputPt.getY());
        } else {
            inputDp = new DirectPosition2D(inputCrs, inputPt.getY(), inputPt.getX());
        }
        transform.transform(inputDp, outputDp);
        Point2D p2d = outputDp.toPoint2D();
        if(CRS.getAxisOrder(outputCrs) == EAST_NORTH)   {
            return new Point2D.Double(p2d.getX(), p2d.getY());  
        } else {
            return new Point2D.Double(p2d.getY(), p2d.getX());  //need to swap coordinates
        }
    }

    public static JsonNode createOrigFormatJson(int epsg, String format, String gridRef, String latitude, String longitude, Double easting, Double northing) throws JsonProcessingException {
        /*
        Scenario: GD49 latlong in DMS Given OrigCoords of: "origCoords":{"epsg":4272,"format":"DMS","longitude":"176 34' 23.01E","latitude":"41 02' 42.51S" } When POSTed to site API  Then stored latlong is 176.57326761,-41.04340655
        Scenario: WGS84 latlong in DD Given OrigCoords of: "origCoords":{"epsg":4326,"format":"DD","longitude":"172.44","latitude":"-45.5675" } When POSTed to site API  Then stored latlong is 172.44,-45.5675
        Scenario: NZTM full coordinates Given OrigCoords of: "origCoords":{"epsg":2193,"format":"EN","easting":"1528677.3","northing":"5413457.7" } When POSTed to site API  Then stored latlong is 172.14641437,-41.42727092
        Scenario: NZMG grid reference Given OrigCoords of: "origCoords":{"epsg":27200,"format":"gridref","gridReference":"U20/962872" } When POSTed to site API  Then stored latlong is 176.32694848,-39.47196732  
         */
        JsonNode origCoord = null;
        ObjectMapper mapper = ObjectMapperWrapper.INSTANCE.get();
        String formatTest = format.toUpperCase();
        if (formatTest.equals("EN")) {
            String jsonString = "{\"epsg\":" + epsg + ",\"format\":\"EN\",\"easting\":" + easting + ",\"northing\":" + northing + " }";
            origCoord = mapper.readTree(jsonString);
        } else if (formatTest.startsWith("DD")) {
            String jsonString = "{\"epsg\":" + epsg + ",\"format\":\"DD\",\"longitude\":\"" + longitude + "\",\"latitude\":\"" + latitude + "\" }";
            origCoord = mapper.readTree(jsonString);
        } else if (formatTest.startsWith("DMS")) {
            String jsonString = "{\"epsg\":" + epsg + ",\"format\":\"DMS\",\"longitude\":\"" + longitude + "\",\"latitude\":\"" + latitude + "\" }";
            origCoord = mapper.readTree(jsonString);
        } else if (formatTest.equals("GRIDREF")) {
            // deal with grid ref
            String jsonString = "{\"epsg\":" + epsg + ",\"format\":\"gridRef\",\"gridReference\":\"" + gridRef + "\"}";
            origCoord = mapper.readTree(jsonString);
        } else {
            throw new InvalidOrigCoordinate("Not a valid format");
        }
        return origCoord;
    }
}
