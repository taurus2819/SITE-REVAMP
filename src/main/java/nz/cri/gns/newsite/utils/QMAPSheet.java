package nz.cri.gns.newsite.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author sorenh
 */
public class QMAPSheet implements MapSeries {

    //TODO - change bboxes to use NZTM
    private enum QMAPSheetBBox{
        KAITAIA("Kaitaia", 2410000, 2570000, 6550000, 6790000),
        WHANGAREI("Whangarei", 2570000, 2730000, 6550000, 6700000),
        AUCKLAND("Auckland", 2570000, 2810000, 6400000, 6550000),
        WAIKATO("Waikato", 2570000, 2730000, 6250000, 6400000),
        ROTORUA("Rotorua", 2730000, 2890000, 6250000, 6400000),
        RAUKUMARA("Raukumara", 2890000, 3050000, 6190000, 6400000),
        TARANAKI("Taranaki", 2570000, 2730000, 6100000, 6250000),
        HAWKES_BAY("Hawkes Bay", 2730000, 2890000, 6100000, 6250000),
        WELLINGTON("Wellington", 2570000, 2730000, 5950000, 6100000),
        WAIRARAPA("Wairarapa", 2730000, 2890000, 5950000, 6100000),
        NELSON("Nelson", 2410000, 2570000, 5950000, 6100000),
        GREYMOUTH("Greymouth", 2250000, 2450000, 5800000, 5950000),
        KAIKOURA("Kaikoura", 2450000, 2650000, 5800000, 5950000),
        HAAST("Haast", 2090000, 2250000, 5650000, 5800000),
        AORAKI("Aoraki", 2250000, 2410000, 5650000, 5800000),
        CHRISTCHURCH("Christchurch", 2410000, 2570000, 5650000, 5800000),
        FIORDLAND_WAKATIPU("Fiordland,Wakatipu", 2090000, 2125000, 5500000, 5636000),   //TODO check with GIS
        WAKATIPU("Wakatipu", 2090000, 2250000, 5500000, 5650000),
        WAITAKI("Waitaki", 2250000, 2410000, 5500000, 5650000),
        FIORDLAND("Fiordland", 1990000, 2090000, 5380000, 5636000),
        MURIHUKU("Murihiku", 2090000, 2250000, 5290000, 5500000),
        DUNEDIN("Dunedin", 2250000, 2410000, 2410000, 5500000),
        ;
        
        private final String name;
        private final int bboxSouth, bboxNorth, bboxEast, bboxWest;
        
        QMAPSheetBBox(String name, int bboxEast, int bboxWest, int bboxSouth, int bboxNorth)    {
            this.name = name;
            this.bboxEast = bboxEast;
            this.bboxWest = bboxWest;
            this.bboxSouth = bboxSouth;
            this.bboxNorth = bboxNorth;
        }
        
        public static Geometry lookup(String name)  {
            for(QMAPSheetBBox bbox : values())  {
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
    }
    
    public static Geometry getBoundingBox(String mapsheet) {
        return QMAPSheetBBox.lookup(mapsheet);
    }

    /**
     * NZMG
     * @return EPSG code
     */
    public static int getDefaultEPSG() {
       return 27200;
    } 
}
