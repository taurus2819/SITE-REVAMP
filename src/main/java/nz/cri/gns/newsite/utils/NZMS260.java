package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import static java.lang.Math.abs;

/**
 * Utility class for converting NZMS260 (New Zealand Map Grid, NZMG, EPSG 27200)
 * gridrefs into full Easting northing
 *
 * @author scaddenp
 */
public class NZMS260 extends MapSheet {

    private static final String validMapSheets = "A44A45B41B42B43B44B45B46B47C40C41C42C43C44C45C46C49C50D38D39D40D41D42D43D44D45D46"
            + "D47D48D49D50E37E38E39E40E41E42E43E44E45E46E47E48E49F36F37F38F39F40F41F42F43F44F45F46F47F48G35G36G37G38G39G40G41G42G43G44"
            + "G45G46G47H34H35H36H37H38H39H40H41H42H43H44H45H46H47I33I34I35I36I37I38I39I40I41I42I43I44I45J31J32J33J34J35J36J37J38J39J40"
            + "J41J42J43J44K29K30K31K32K33K34K35K36K37K38K39L01L25L26L27L28L29L30L31L32L33L34L35L36L37M02M24M25M26M27M28M29M30M31M32M33"
            + "M34M35M36M37N02N03N04N05N24N25N26N27N28N29N30N31N32N33N34N36N37O03O04O05O06O07O26O27O28O29O30O31O32O33P04P05P06P07P08P09"
            + "P19P20P21P25P26P27P28P29P30P31Q04Q05Q06Q07Q08Q09Q10Q11Q12Q15Q18Q19Q20Q21Q22Q26Q27Q29R06R07R08R09R10R11R12R13R14R15R16R17"
            + "R18R19R20R21R22R23R25R26R27R28S07S08S09S10S11S12S13S14S15S16S17S18S19S20S21S22S23S24S25S26S27S28T08T09T10T11T12T13T14T15"
            + "T16T17T18T19T20T21T22T23T24T25T26T27T28U10U11U12U13U14U15U16U17U18U19U20U21U22U23U24U25U26V14V15V16V17V18V19V20V21V22V23"
            + "V24W13W14W15W16W17W18W19W20W21W22X14X15X16X17X18X19X20Y14Y15Y16Y17Y18Y19Y20Z14Z15Z16Z17";

    private static NZMS260 _instance;

    private NZMS260() {
    }
   
    public static NZMS260 getInstance() {
        if (_instance == null) {
            _instance = new NZMS260();
        }
        return _instance;
    }

    private static boolean isValidMapSheet(String mapSheet) {
        return (validMapSheets.indexOf(mapSheet) >= 0);
    }

    /**
     * Adds the given truncated measurement to the start northing/easting given.
     * eg 2090000 + 93400 = 2093400<br> eg 2090000 + 02000 = 2102000<br>
     */
    private static long addToStart(long start, int local) {
        int iStart = (int) (start % 100000.0);
        if (local >= iStart) {
            return start - iStart + local;
        } else {
            return start - iStart + local + 100000;
        }
    }

    /**
     * Function to convert NZMS260 (NZ Map Grid) truncated coordinates derived
     * from a grid reference into full coordinates
     *
     * @param epsg 27200
     * @param mapsheet The mapsheet reference. eg U34
     * @param truncEast the 4-figure truncated easting value (3 figure truncated
     * eastings should be multiplied by 10 before passing to this routine)
     * @param truncNorth the 4-figure truncated northing value (3 figure
     * truncated northings should be multiplied by 10 before passing to this
     * routine)
     * @return a Point2D x,y containing the full easting and northings
     */
    public Point2D getFullCoordinates(int epsg, String mapsheet, int truncEast, int truncNorth) {
        if (!isValidMapSheet(mapsheet)) {
            throw new InvalidOrigCoordinate("Not within the valid mapsheet list for epsg: " + epsg);
        }
        int letter = mapsheet.toUpperCase().charAt(0) - 'A';
        long sheetEBound = 1970000 + 40000 * letter;
        long truncEBound = (sheetEBound / 10) % 10000;
        if (truncEBound <= 6000) {
            if (truncEast < truncEBound || truncEast > (truncEBound + 4000)) {
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of epsg: " + epsg);
            }
        } else {
            if (truncEast < truncEBound && truncEast > (truncEBound - 6000)) {
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of epsg: " + epsg);
            }
        }
        long easting = addToStart(sheetEBound, truncEast * 10);
        int number = Integer.parseInt(mapsheet.substring(1));
        long sheetNBound = 6790000 - 30000 * number;
        long truncNBound = (sheetNBound / 10) % 10000;
        if (truncNBound <= 7000) {
            if (truncNorth < truncNBound || truncNorth > (truncNBound + 3000)) {
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of epsg: " + epsg);
            }
        } else {
            if (truncNorth < truncNBound && truncNorth > (truncNBound - 7000)) {
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of epsg: " + epsg);
            }
        }
        long northing = addToStart(sheetNBound, truncNorth * 10);
        return new Point2D.Double(easting * 1.0d, northing * 1.0d);
    }

    /**
     * Taken over from Oracle DB
     *
     * @param easting
     * @param northing
     * @return
     */
    @Override
    public String lookupMapSheet(double easting, double northing) {
        int i = (int)((easting-1930000)/40000);
        int j = (int)((6820000-northing)/30000);

        String result = null;
        Double eastingVal = Double.valueOf(easting);
        Double northingVal = Double.valueOf(northing);
        if(eastingVal.toString().endsWith("E7") && !northingVal.toString().endsWith("E7")){
            result = "RS";
        }else if(northingVal.toString().endsWith("E7") && eastingVal.toString().endsWith("E7")){
            result = "RS";
        }else {
            result = String.format("%s%s%d", Character.toString((char) ('A' + i - 1)), j < 10 ? "0" : "", j > i ? j : i);
        }
        return result;
    }
    
    public Geometry getBoundingBox(String mapsheet) {
        char letter = mapsheet.charAt(0);
        if (!isValidMapSheet(""+letter)) {
            throw new InvalidOrigCoordinate("Invalid mapsheet reference letter for mapsheet: " + mapsheet);
        }
        int i = letter - (char)('A') + 1;
        double bboxWest = (i * 40000) + 1930000;
        double bboxEast = bboxWest + 40000;
        
        int sheet;
        try {
            sheet = Integer.parseInt(mapsheet.substring(1));
        } catch (NumberFormatException e) {
            throw new InvalidOrigCoordinate("Invalid sheet no. for mapsheet: " + mapsheet);
        }
        double bboxNorth =  6820000 - (sheet * 30000);
        double bboxSouth = bboxNorth - 30000;
        System.err.println(String.format("BBOX: LL %f,%f, UR %f %f", bboxWest, bboxSouth, bboxEast, bboxNorth));
        
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), getDefaultEPSG());
        
        
        
        Polygon polygon = factory.createPolygon(factory.createLinearRing(new Coordinate[]{
            new Coordinate(bboxWest, bboxSouth),
            new Coordinate(bboxWest, bboxNorth),
            new Coordinate(bboxEast, bboxNorth),
            new Coordinate(bboxEast, bboxSouth),
            new Coordinate(bboxWest, bboxSouth),}), null);
        return polygon;
    }
    
    public static int getDefaultEPSG() {
       return 27200;
    } 

    @Override
    public int getMapsheetLookupEPSG() {
        return getDefaultEPSG();
    }
}
