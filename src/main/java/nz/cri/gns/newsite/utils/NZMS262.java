package nz.cri.gns.newsite.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

/**
 *
 * @author sorenh
 */
public class NZMS262 extends MapSheet implements MapSeries {

    private static NZMS262 _instance; 
    
    private NZMS262(){};
   
    public static NZMS262 getInstance()  {
        if(_instance == null)   {
            _instance = new NZMS262();
        }
        return _instance;
    }
    
    private enum NZMS262SheetBBox{
        NORTHLAND("Northland", 2410000, 2810000, 6550000, 6790000),
        AUCKLAND("Auckland", 2570000, 2810000, 6400000, 6550000),
        WAIKATO("Waikato", 2610000, 2810000, 6250000, 6400000),
        EAST_CAPE("East Cape", 2810000, 3010000, 6250000, 6430000),
        TARANAKI("Taranaki", 2570000, 2770000, 6100000, 6250000),
        HAWKES_BAY("Hawkes Bay", 2770000, 2970000, 6100000, 6250000),
        WELLINGTON("Wellington", 2650000, 2850000, 5950000, 6100000),
        NELSON("Nelson", 2410000, 2650000, 5950000, 6100000),
        GREY("Grey", 2290000, 2450000, 5800000, 5950000),
        KAIKOURA("Kaikoura", 2450000, 2650000, 5800000, 5950000),
        MT_COOK("Mt Cook", 2090000, 2330000, 5650000, 5800000),
        CHRISTCHURCH("Christchurch", 2330000, 2530000, 5650000, 5800000),
        TE_ANAU("Te Anau", 2010000, 2210000, 5500000, 5650000),
        WAITAKI("Waitaki", 2210000, 2410000, 5500000, 5650000),
        INVERCARGILL("Invercargill", 1970000, 2170000, 5290000, 5500000),
        DUNEDIN("Dunedin", 2170000, 2370000, 5350000, 5500000),
        ;
        
        private final String name;
        private final int bboxSouth, bboxNorth, bboxEast, bboxWest;
        
        NZMS262SheetBBox(String name, int bboxEast, int bboxWest, int bboxSouth, int bboxNorth)    {
            this.name = name;
            this.bboxEast = bboxEast;
            this.bboxWest = bboxWest;
            this.bboxSouth = bboxSouth;
            this.bboxNorth = bboxNorth;
        }
        
        public static Geometry lookup(String name)  {
            for(NZMS262SheetBBox bbox : values())  {
                if(bbox.name.equals(name))   {
                    return bbox.getBBox();
                }
            }
            return null;
        }
        
        public Geometry getBBox()   {
            final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), getDefaultEPSG());
            Polygon polygon = factory.createPolygon(factory.createLinearRing(new Coordinate[]{
                new Coordinate(bboxWest, bboxSouth),
                new Coordinate(bboxWest, bboxNorth),
                new Coordinate(bboxEast, bboxNorth),
                new Coordinate(bboxEast, bboxSouth),
                new Coordinate(bboxWest, bboxSouth),
            }), null);   
            return polygon;
        }
        
        public static String lookupSheet(Geometry location)    {
            for(NZMS262SheetBBox bbox : values())  {
                double easting = location.getCoordinate().getX();
                double northing = location.getCoordinate().getY();
                
                if(bbox.getBBox().contains(location))   {
                    return bbox.name;
                }
            }
            return null;
        }
    }
    
    public Geometry getBoundingBox(String mapsheet) {
        return NZMS262SheetBBox.lookup(mapsheet);
    }
    
    public String getBoundingBoxGeoJson(String mapsheet) {
        Geometry geom = getBoundingBox(mapsheet);
        if (geom == null) return "";
        return new GeoJsonWriter().write(geom);
    }

    @Override
    public String lookupMapSheet(double easting, double northing) {
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(easting,northing));
        point.setSRID(getMapsheetLookupEPSG());
        return NZMS262SheetBBox.lookupSheet(point);
    }
    
    /**
     * NZMG
     * @return EPSG code
     */
    public static int getDefaultEPSG() {
       return 27200;
    } 
    
    @Override
    public int getMapsheetLookupEPSG() {
        return 27200;
    }
}
