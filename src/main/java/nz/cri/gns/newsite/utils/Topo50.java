package nz.cri.gns.newsite.utils;

import java.awt.geom.Point2D;
import nz.cri.gns.newsite.exception.InvalidOrigCoordinate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Utility class for converting NZTopo50 (NZ Transverse Mercator, NZTM, EPSG
 * 2193) gridrefs into full Easting northing
 *
 * @author scaddenp
 */
public class Topo50 extends MapSheet implements MapSeries {

    static final String NZTMSL = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    static final String validMapReference = "ABC";
    
    private static Topo50 _instance; 
    
    private Topo50(){};
   
    public static Topo50 getInstance()  {
        if(_instance == null)   {
            _instance = new Topo50();
        }
        return _instance;
    }

    private boolean isValidMapSheet(char mapSheetReferenceChar) {
        return (validMapReference.indexOf(mapSheetReferenceChar) == -1);
    }

    /**
     * Function to convert topo50 truncated coordinates derived from a grid
     * reference into full coordinates
     *
     * @param epsg 2193
     * @param mapsheet The mapsheet reference. eg BE33, CF04
     * @param truncEast the 4-figure truncated easting value (3 figure truncated
     * eastings should be multiplied by 10 before passing to this routine)
     * @param truncNorth the 4-figure truncated northing value (3 figure
     * truncated northings should be multiplied by 10 before passing to this
     * routine)
     * @return a Point2D x,y containing the full easting and northings
     */
    public Point2D getFullCoordinates(int epsg, String mapsheet, int truncEast, int truncNorth) {
        char s1 = mapsheet.charAt(0);
        if (isValidMapSheet(s1)) {
            throw new InvalidOrigCoordinate("Invalid mapsheet reference letter for this epsg: " + epsg);
        }
        int sheet;
        try {
            sheet = Integer.parseInt(mapsheet.substring(2));
        } catch (Exception e) {
            throw new InvalidOrigCoordinate("invalid sheet no. for this epsg: " + epsg);
        }
        int nid = NZTMSL.indexOf(mapsheet.charAt(1)) + 1;
        if (sheet < 4 || sheet > 45 || nid < 1 || nid > 24) {
            throw new InvalidOrigCoordinate("grid reference is outside the bounds of this epsg: " + epsg);
        }
        int southS;
        switch (s1) {
            case 'A':
                southS = 6810000;
                break;
            case 'B':
                southS = 5946000;
                break;
            case 'C':
                southS = 5082000;
                break;
            default:
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of this epsg: " + epsg);
        }
        southS = southS - nid * 36000;
        int southST = Math.floorDiv(southS, 100000) * 100000;
        double north = truncNorth * 10.0d + southST;
        int eastS = sheet * 24000 + 988000;
        int eastST = Math.floorDiv(eastS, 100000) * 100000;
        double east = truncEast * 10 + eastST;
        return new Point2D.Double(east, north);
    }

    /**
     * Generates the bounding geometry of the specified mapsheet in NZMG
     *
     * @param mapsheet
     * @return
     */
    public Geometry getBoundingBox(String mapsheet) {
        //TODO #1 check in GIS and add test cases
        //TODO #2 handle irregular map sheet names, like "BD39ptBE39"
        //TODO #3 check if this is actually precise enough, as current DB function has different increments

        char s1 = mapsheet.charAt(0);
        if (isValidMapSheet(s1)) {
            throw new InvalidOrigCoordinate("Invalid mapsheet reference letter for mapsheet: " + mapsheet);
        }
        int sheet;
        try {
            sheet = Integer.parseInt(mapsheet.substring(2));
        } catch (NumberFormatException e) {
            throw new InvalidOrigCoordinate("Invalid sheet no. for mapsheet: " + mapsheet);
        }
        int nid = NZTMSL.indexOf(mapsheet.charAt(1)) + 1;
        if (sheet < 4 || sheet > 45 || nid < 1 || nid > 24) {
            throw new InvalidOrigCoordinate("Grid reference is outside the bounds of this mapsheet: " + mapsheet);
        }
        int bboxSouth, bboxNorth, bboxEast, bboxWest;
        switch (s1) {
            case 'A':
                bboxSouth = 6810000;
                break;
            case 'B':
                bboxSouth = 5946000;
                break;
            case 'C':
                bboxSouth = 5082000;
                break;
            default:
                throw new InvalidOrigCoordinate("grid reference is outside the bounds of this mapsheet: " + mapsheet);
        }
        bboxSouth = bboxSouth - nid * 36000;
        bboxNorth = bboxSouth + 36000;

        bboxWest = sheet * 24000 + 988000;
        bboxEast = bboxWest + 24000;

        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), getDefaultEPSG());

        Polygon polygon = factory.createPolygon(factory.createLinearRing(new Coordinate[]{
            new Coordinate(bboxWest, bboxSouth),
            new Coordinate(bboxWest, bboxNorth),
            new Coordinate(bboxEast, bboxNorth),
            new Coordinate(bboxEast, bboxSouth),
            new Coordinate(bboxWest, bboxSouth),}), null);
        return polygon;
    }

    /**
     * Taken over from Oracle DB; a smarter version should replace this basic values lookup
     * @param easting
     * @param northing
     * @return 
     */
    @Override
    public String lookupMapSheet(double easting, double northing) {
        if (easting >= 2415040 && northing >= 6760520 && easting <= 2439181 && northing <= 6796448) {
            return "AS21/AS22";
        } else if (easting >= 2474954 && northing >= 6724296 && easting <= 2499078 && northing <= 6760237) {
            return "AT24";
        } else if (easting >= 2498967 && northing >= 6724222 && easting <= 2523086 && northing <= 6760163) {
            return "AT25";
        } else if (easting >= 2498860 && northing >= 6688201 && easting <= 2522978 && northing <= 6724150) {
            return "AU25";
        } else if (easting >= 2522874 && northing >= 6688132 && easting <= 2546988 && northing <= 6724080) {
            return "AU26";
        } else if (easting >= 2546886 && northing >= 6688064 && easting <= 2570997 && northing <= 6724012) {
            return "AU27";
        } else if (easting >= 2570865 && northing >= 6675993 && easting <= 2594972 && northing <= 6711942) {
            return "AU28ptAV28";
        } else if (easting >= 2594875 && northing >= 6675928 && easting <= 2618980 && northing <= 6711876) {
            return "AU29ptAV29";
        } else if (easting >= 2506763 && northing >= 6652154 && easting <= 2530878 && northing <= 6688109) {
            return "AV25ptAV26";
        } else if (easting >= 2522773 && northing >= 6652110 && easting <= 2546886 && northing <= 6688064) {
            return "AV26";
        } else if (easting >= 2546788 && northing >= 6652045 && easting <= 2570897 && northing <= 6687998) {
            return "AV27";
        } else if (easting >= 2570800 && northing >= 6651981 && easting <= 2594907 && northing <= 6687933) {
            return "AV28";
        } else if (easting >= 2594811 && northing >= 6651918 && easting <= 2618916 && northing <= 6687868) {
            return "AV29";
        } else if (easting >= 2618821 && northing >= 6651856 && easting <= 2642924 && northing <= 6687804) {
            return "AV30";
        } else if (easting >= 2522678 && northing >= 6616086 && easting <= 2546788 && northing <= 6652045) {
            return "AW26";
        } else if (easting >= 2546694 && northing >= 6616024 && easting <= 2570800 && northing <= 6651981) {
            return "AW27";
        } else if (easting >= 2570707 && northing >= 6615963 && easting <= 2594811 && northing <= 6651918) {
            return "AW28";
        } else if (easting >= 2594719 && northing >= 6615903 && easting <= 2618821 && northing <= 6651856) {
            return "AW29";
        } else if (easting >= 2618729 && northing >= 6615842 && easting <= 2642829 && northing <= 6651793) {
            return "AW30";
        } else if (easting >= 2642736 && northing >= 6615782 && easting <= 2666836 && northing <= 6651730) {
            return "AW31";
        } else if (easting >= 2666743 && northing >= 6615722 && easting <= 2690841 && northing <= 6651667) {
            return "AW32";
        } else if (easting >= 2546604 && northing >= 6580001 && easting <= 2570707 && northing <= 6615963) {
            return "AX27";
        } else if (easting >= 2570619 && northing >= 6579944 && easting <= 2594719 && northing <= 6615903) {
            return "AX28";
        } else if (easting >= 2594631 && northing >= 6579887 && easting <= 2618729 && northing <= 6615842) {
            return "AX29";
        } else if (easting >= 2618640 && northing >= 6579829 && easting <= 2642736 && northing <= 6615782) {
            return "AX30";
        } else if (easting >= 2642648 && northing >= 6579772 && easting <= 2666743 && northing <= 6615722) {
            return "AX31";
        } else if (easting >= 2658623 && northing >= 6567731 && easting <= 2682716 && northing <= 6603679) {
            return "AX32ptsAX31,AY31,AY32";
        } else if (easting >= 2690657 && northing >= 6579656 && easting <= 2714751 && northing <= 6615599) {
            return "AX33";
        } else if (easting >= 2570535 && northing >= 6543924 && easting <= 2594631 && northing <= 6579887) {
            return "AY28";
        } else if (easting >= 2594547 && northing >= 6543870 && easting <= 2618640 && northing <= 6579829) {
            return "AY29";
        } else if (easting >= 2618557 && northing >= 6543816 && easting <= 2642648 && northing <= 6579772) {
            return "AY30";
        } else if (easting >= 2642564 && northing >= 6543762 && easting <= 2666654 && northing <= 6579714) {
            return "AY31";
        } else if (easting >= 2666569 && northing >= 6543708 && easting <= 2690657 && northing <= 6579656) {
            return "AY32";
        } else if (easting >= 2690571 && northing >= 6543653 && easting <= 2714659 && northing <= 6579596) {
            return "AY33";
        } else if (easting >= 2714572 && northing >= 6543597 && easting <= 2738659 && northing <= 6579536) {
            return "AY34";
        } else if (easting >= 2594469 && northing >= 6507854 && easting <= 2618557 && northing <= 6543816) {
            return "AZ29";
        } else if (easting >= 2618479 && northing >= 6507804 && easting <= 2642564 && northing <= 6543762) {
            return "AZ30";
        } else if (easting >= 2642485 && northing >= 6507754 && easting <= 2666569 && northing <= 6543708) {
            return "AZ31";
        } else if (easting >= 2666489 && northing >= 6507703 && easting <= 2690571 && northing <= 6543653) {
            return "AZ32";
        } else if (easting >= 2714490 && northing >= 6507599 && easting <= 2738570 && northing <= 6543540) {
            return "AZ34";
        } else if (easting >= 2738486 && northing >= 6507546 && easting <= 2762565 && northing <= 6543481) {
            return "AZ35";
        } else if (easting >= 2754462 && northing >= 6498512 && easting <= 2778538 && northing <= 6534445) {
            return "AZ36ptsAZ35,BA35,BA36";
        } else if (easting >= 2618406 && northing >= 6471793 && easting <= 2642485 && northing <= 6507754) {
            return "BA30";
        } else if (easting >= 2642412 && northing >= 6471746 && easting <= 2666489 && northing <= 6507703) {
            return "BA31";
        } else if (easting >= 2666415 && northing >= 6471699 && easting <= 2690491 && northing <= 6507651) {
            return "BA32";
        } else if (easting >= 2690416 && northing >= 6471652 && easting <= 2714490 && northing <= 6507599) {
            return "BA33";
        } else if (easting >= 2714414 && northing >= 6471604 && easting <= 2738486 && northing <= 6507546) {
            return "BA34";
        } else if (easting >= 2738408 && northing >= 6471555 && easting <= 2762480 && northing <= 6507492) {
            return "BA35";
        } else if (easting >= 2754403 && northing >= 6471521 && easting <= 2778474 && northing <= 6507455) {
            return "BA36ptBA35";
        } else if (easting >= 2626342 && northing >= 6435768 && easting <= 2650414 && northing <= 6471731) {
            return "BB30ptBB31";
        } else if (easting >= 2642345 && northing >= 6435740 && easting <= 2666415 && northing <= 6471699) {
            return "BB31";
        } else if (easting >= 2666348 && northing >= 6435697 && easting <= 2690416 && northing <= 6471652) {
            return "BB32";
        } else if (easting >= 2690347 && northing >= 6435654 && easting <= 2714414 && northing <= 6471604) {
            return "BB33";
        } else if (easting >= 2714344 && northing >= 6435610 && easting <= 2738408 && northing <= 6471555) {
            return "BB34";
        } else if (easting >= 2738338 && northing >= 6435565 && easting <= 2762400 && northing <= 6471505) {
            return "BB35";
        } else if (easting >= 2762328 && northing >= 6435520 && easting <= 2786389 && northing <= 6471453) {
            return "BB36";
        } else if (easting >= 2778320 && northing >= 6435489 && easting <= 2802380 && northing <= 6471419) {
            return "BB37ptBB36";
        } else if (easting >= 2642285 && northing >= 6399735 && easting <= 2666348 && northing <= 6435697) {
            return "BC31";
        } else if (easting >= 2666287 && northing >= 6399697 && easting <= 2690347 && northing <= 6435654) {
            return "BC32";
        } else if (easting >= 2690285 && northing >= 6399658 && easting <= 2714344 && northing <= 6435610) {
            return "BC33";
        } else if (easting >= 2714281 && northing >= 6399618 && easting <= 2738338 && northing <= 6435565) {
            return "BC34";
        } else if (easting >= 2738273 && northing >= 6399578 && easting <= 2762328 && northing <= 6435520) {
            return "BC35";
        } else if (easting >= 2762263 && northing >= 6399537 && easting <= 2786315 && northing <= 6435474) {
            return "BC36";
        } else if (easting >= 2786249 && northing >= 6399496 && easting <= 2810299 && northing <= 6435426) {
            return "BC37";
        } else if (easting >= 2866155 && northing >= 6387367 && easting <= 2890194 && northing <= 6423276) {
            return "BC40ptBD40";
        } else if (easting >= 2650232 && northing >= 6363720 && easting <= 2674286 && northing <= 6399684) {
            return "BD31ptBD32";
        } else if (easting >= 2666232 && northing >= 6363697 && easting <= 2690285 && northing <= 6399658) {
            return "BD32";
        } else if (easting >= 2690230 && northing >= 6363662 && easting <= 2714281 && northing <= 6399618) {
            return "BD33";
        } else if (easting >= 2714225 && northing >= 6363628 && easting <= 2738273 && northing <= 6399578) {
            return "BD34";
        } else if (easting >= 2738217 && northing >= 6363592 && easting <= 2762263 && northing <= 6399537) {
            return "BD35";
        } else if (easting >= 2762205 && northing >= 6363557 && easting <= 2786249 && northing <= 6399496) {
            return "BD36";
        } else if (easting >= 2786190 && northing >= 6363520 && easting <= 2810232 && northing <= 6399454) {
            return "BD37";
        } else if (easting >= 2810172 && northing >= 6363484 && easting <= 2834211 && northing <= 6399411) {
            return "BD38";
        } else if (easting >= 2834132 && northing >= 6351458 && easting <= 2858164 && northing <= 6387381) {
            return "BD39ptBE39";
        } else if (easting >= 2858105 && northing >= 6351422 && easting <= 2882135 && northing <= 6387338) {
            return "BD40ptBE40";
        } else if (easting >= 2906060 && northing >= 6363331 && easting <= 2930086 && northing <= 6399230) {
            return "BD42";
        } else if (easting >= 2930021 && northing >= 6363291 && easting <= 2954044 && northing <= 6399182) {
            return "BD43";
        } else if (easting >= 2953977 && northing >= 6363251 && easting <= 2977996 && northing <= 6399133) {
            return "BD44";
        } else if (easting >= 2977928 && northing >= 6363210 && easting <= 3001942 && northing <= 6399083) {
            return "BD45";
        } else if (easting >= 2642184 && northing >= 6327728 && easting <= 2666232 && northing <= 6363697) {
            return "BE31";
        } else if (easting >= 2666184 && northing >= 6327698 && easting <= 2690230 && northing <= 6363662) {
            return "BE32";
        } else if (easting >= 2690181 && northing >= 6327668 && easting <= 2714225 && northing <= 6363628) {
            return "BE33";
        } else if (easting >= 2714176 && northing >= 6327638 && easting <= 2738217 && northing <= 6363592) {
            return "BE34";
        } else if (easting >= 2738167 && northing >= 6327608 && easting <= 2762205 && northing <= 6363557) {
            return "BE35";
        } else if (easting >= 2762155 && northing >= 6327577 && easting <= 2786190 && northing <= 6363520) {
            return "BE36";
        } else if (easting >= 2786139 && northing >= 6327546 && easting <= 2810172 && northing <= 6363484) {
            return "BE37";
        } else if (easting >= 2810120 && northing >= 6327514 && easting <= 2834150 && northing <= 6363446) {
            return "BE38";
        } else if (easting >= 2834098 && northing >= 6327483 && easting <= 2858124 && northing <= 6363408) {
            return "BE39";
        } else if (easting >= 2858071 && northing >= 6327451 && easting <= 2882094 && northing <= 6363370) {
            return "BE40";
        } else if (easting >= 2882041 && northing >= 6327419 && easting <= 2906060 && northing <= 6363331) {
            return "BE41";
        } else if (easting >= 2906006 && northing >= 6327386 && easting <= 2930021 && northing <= 6363291) {
            return "BE42";
        } else if (easting >= 2929966 && northing >= 6327354 && easting <= 2953977 && northing <= 6363251) {
            return "BE43";
        } else if (easting >= 2953922 && northing >= 6327321 && easting <= 2977928 && northing <= 6363210) {
            return "BE44";
        } else if (easting >= 2977872 && northing >= 6327288 && easting <= 3001873 && northing <= 6363169) {
            return "BE45";
        } else if (easting >= 2642143 && northing >= 6291725 && easting <= 2666184 && northing <= 6327698) {
            return "BF31";
        } else if (easting >= 2666143 && northing >= 6291700 && easting <= 2690181 && northing <= 6327668) {
            return "BF32";
        } else if (easting >= 2690140 && northing >= 6291675 && easting <= 2714176 && northing <= 6327638) {
            return "BF33";
        } else if (easting >= 2714134 && northing >= 6291649 && easting <= 2738167 && northing <= 6327608) {
            return "BF34";
        } else if (easting >= 2738125 && northing >= 6291623 && easting <= 2762155 && northing <= 6327577) {
            return "BF35";
        } else if (easting >= 2762112 && northing >= 6291598 && easting <= 2786139 && northing <= 6327546) {
            return "BF36";
        } else if (easting >= 2786096 && northing >= 6291572 && easting <= 2810120 && northing <= 6327514) {
            return "BF37";
        } else if (easting >= 2810077 && northing >= 6291546 && easting <= 2834098 && northing <= 6327483) {
            return "BF38";
        } else if (easting >= 2834055 && northing >= 6291520 && easting <= 2858071 && northing <= 6327451) {
            return "BF39";
        } else if (easting >= 2858028 && northing >= 6291494 && easting <= 2882041 && northing <= 6327419) {
            return "BF40";
        } else if (easting >= 2881997 && northing >= 6291468 && easting <= 2906006 && northing <= 6327386) {
            return "BF41";
        } else if (easting >= 2905962 && northing >= 6291442 && easting <= 2929966 && northing <= 6327354) {
            return "BF42";
        } else if (easting >= 2929923 && northing >= 6291417 && easting <= 2953922 && northing <= 6327321) {
            return "BF43";
        } else if (easting >= 2953879 && northing >= 6291391 && easting <= 2977872 && northing <= 6327288) {
            return "BF44";
        } else if (easting >= 2969846 && northing >= 6291375 && easting <= 2993836 && northing <= 6327266) {
            return "BF45ptBF44";
        } else if (easting >= 2618096 && northing >= 6243743 && easting <= 2642131 && northing <= 6279725) {
            return "BG30ptBH30";
        } else if (easting >= 2642109 && northing >= 6255724 && easting <= 2666143 && northing <= 6291700) {
            return "BG31";
        } else if (easting >= 2666108 && northing >= 6255703 && easting <= 2690140 && northing <= 6291675) {
            return "BG32";
        } else if (easting >= 2690105 && northing >= 6255682 && easting <= 2714134 && northing <= 6291649) {
            return "BG33";
        } else if (easting >= 2714099 && northing >= 6255661 && easting <= 2738125 && northing <= 6291623) {
            return "BG34";
        } else if (easting >= 2738089 && northing >= 6255640 && easting <= 2762112 && northing <= 6291598) {
            return "BG35";
        } else if (easting >= 2762077 && northing >= 6255619 && easting <= 2786096 && northing <= 6291572) {
            return "BG36";
        } else if (easting >= 2786061 && northing >= 6255598 && easting <= 2810077 && northing <= 6291546) {
            return "BG37";
        } else if (easting >= 2810042 && northing >= 6255577 && easting <= 2834055 && northing <= 6291520) {
            return "BG38";
        } else if (easting >= 2834020 && northing >= 6255556 && easting <= 2858028 && northing <= 6291494) {
            return "BG39";
        } else if (easting >= 2857994 && northing >= 6255536 && easting <= 2881997 && northing <= 6291468) {
            return "BG40";
        } else if (easting >= 2881963 && northing >= 6255517 && easting <= 2905962 && northing <= 6291442) {
            return "BG41";
        } else if (easting >= 2905929 && northing >= 6255497 && easting <= 2929923 && northing <= 6291417) {
            return "BG42";
        } else if (easting >= 2929890 && northing >= 6255479 && easting <= 2953879 && northing <= 6291391) {
            return "BG43";
        } else if (easting >= 2953847 && northing >= 6255461 && easting <= 2977829 && northing <= 6291367) {
            return "BG44";
        } else if (easting >= 2570067 && northing >= 6219772 && easting <= 2594101 && northing <= 6255765) {
            return "BH28";
        } else if (easting >= 2594074 && northing >= 6219756 && easting <= 2618106 && northing <= 6255744) {
            return "BH29";
        } else if (easting >= 2618079 && northing >= 6219739 && easting <= 2642109 && northing <= 6255724) {
            return "BH30";
        } else if (easting >= 2642081 && northing >= 6219723 && easting <= 2666108 && northing <= 6255703) {
            return "BH31";
        } else if (easting >= 2666080 && northing >= 6219706 && easting <= 2690105 && northing <= 6255682) {
            return "BH32";
        } else if (easting >= 2690076 && northing >= 6219689 && easting <= 2714099 && northing <= 6255661) {
            return "BH33";
        } else if (easting >= 2714070 && northing >= 6219672 && easting <= 2738089 && northing <= 6255640) {
            return "BH34";
        } else if (easting >= 2738061 && northing >= 6219656 && easting <= 2762077 && northing <= 6255619) {
            return "BH35";
        } else if (easting >= 2762049 && northing >= 6219639 && easting <= 2786061 && northing <= 6255598) {
            return "BH36";
        } else if (easting >= 2786034 && northing >= 6219623 && easting <= 2810042 && northing <= 6255577) {
            return "BH37";
        } else if (easting >= 2810015 && northing >= 6219608 && easting <= 2834020 && northing <= 6255556) {
            return "BH38";
        } else if (easting >= 2833994 && northing >= 6219592 && easting <= 2857994 && northing <= 6255536) {
            return "BH39";
        } else if (easting >= 2857968 && northing >= 6219578 && easting <= 2881963 && northing <= 6255517) {
            return "BH40";
        } else if (easting >= 2881939 && northing >= 6219564 && easting <= 2905929 && northing <= 6255497) {
            return "BH41";
        } else if (easting >= 2905905 && northing >= 6219551 && easting <= 2929890 && northing <= 6255479) {
            return "BH42";
        } else if (easting >= 2929868 && northing >= 6219539 && easting <= 2953847 && northing <= 6255461) {
            return "BH43";
        } else if (easting >= 2570046 && northing >= 6183760 && easting <= 2594074 && northing <= 6219756) {
            return "BJ28";
        } else if (easting >= 2594052 && northing >= 6183748 && easting <= 2618079 && northing <= 6219739) {
            return "BJ29";
        } else if (easting >= 2618057 && northing >= 6183735 && easting <= 2642081 && northing <= 6219723) {
            return "BJ30";
        } else if (easting >= 2642058 && northing >= 6183722 && easting <= 2666080 && northing <= 6219706) {
            return "BJ31";
        } else if (easting >= 2666058 && northing >= 6183709 && easting <= 2690076 && northing <= 6219689) {
            return "BJ32";
        } else if (easting >= 2690054 && northing >= 6183696 && easting <= 2714070 && northing <= 6219672) {
            return "BJ33";
        } else if (easting >= 2714048 && northing >= 6183683 && easting <= 2738061 && northing <= 6219656) {
            return "BJ34";
        } else if (easting >= 2738040 && northing >= 6183671 && easting <= 2762049 && northing <= 6219639) {
            return "BJ35";
        } else if (easting >= 2762028 && northing >= 6183659 && easting <= 2786034 && northing <= 6219623) {
            return "BJ36";
        } else if (easting >= 2786014 && northing >= 6183648 && easting <= 2810015 && northing <= 6219608) {
            return "BJ37";
        } else if (easting >= 2809996 && northing >= 6183637 && easting <= 2833994 && northing <= 6219592) {
            return "BJ38";
        } else if (easting >= 2833975 && northing >= 6183627 && easting <= 2857968 && northing <= 6219578) {
            return "BJ39";
        } else if (easting >= 2849959 && northing >= 6183621 && easting <= 2873949 && northing <= 6219569) {
            return "BJ40ptBJ39";
        } else if (easting >= 2921871 && northing >= 6195581 && easting <= 2945846 && northing <= 6231510) {
            return "BJ43ptsBJ42,BH42,BH43";
        } else if (easting >= 2570035 && northing >= 6159753 && easting <= 2594059 && northing <= 6195750) {
            return "BK28ptBJ28";
        } else if (easting >= 2594036 && northing >= 6147740 && easting <= 2618057 && northing <= 6183735) {
            return "BK29";
        } else if (easting >= 2618040 && northing >= 6147731 && easting <= 2642058 && northing <= 6183722) {
            return "BK30";
        } else if (easting >= 2642042 && northing >= 6147721 && easting <= 2666058 && northing <= 6183709) {
            return "BK31";
        } else if (easting >= 2666041 && northing >= 6147712 && easting <= 2690054 && northing <= 6183696) {
            return "BK32";
        } else if (easting >= 2690038 && northing >= 6147703 && easting <= 2714048 && northing <= 6183683) {
            return "BK33";
        } else if (easting >= 2714033 && northing >= 6147694 && easting <= 2738040 && northing <= 6183671) {
            return "BK34";
        } else if (easting >= 2738025 && northing >= 6147686 && easting <= 2762028 && northing <= 6183659) {
            return "BK35";
        } else if (easting >= 2762014 && northing >= 6147678 && easting <= 2786014 && northing <= 6183648) {
            return "BK36";
        } else if (easting >= 2786001 && northing >= 6147671 && easting <= 2809996 && northing <= 6183637) {
            return "BK37";
        } else if (easting >= 2809984 && northing >= 6147665 && easting <= 2833975 && northing <= 6183627) {
            return "BK38";
        } else if (easting >= 2833965 && northing >= 6147660 && easting <= 2857951 && northing <= 6183618) {
            return "BK39";
        } else if (easting >= 2849950 && northing >= 6147657 && easting <= 2873933 && northing <= 6183613) {
            return "BK40ptBK39";
        } else if (easting >= 2642033 && northing >= 6123721 && easting <= 2666046 && northing <= 6159711) {
            return "BL31ptBK31";
        } else if (easting >= 2666030 && northing >= 6111714 && easting <= 2690038 && northing <= 6147703) {
            return "BL32";
        } else if (easting >= 2690027 && northing >= 6111709 && easting <= 2714033 && northing <= 6147694) {
            return "BL33";
        } else if (easting >= 2714023 && northing >= 6111703 && easting <= 2738025 && northing <= 6147686) {
            return "BL34";
        } else if (easting >= 2738016 && northing >= 6111699 && easting <= 2762014 && northing <= 6147678) {
            return "BL35";
        } else if (easting >= 2762006 && northing >= 6111695 && easting <= 2786001 && northing <= 6147671) {
            return "BL36";
        } else if (easting >= 2785994 && northing >= 6111692 && easting <= 2809984 && northing <= 6147665) {
            return "BL37";
        } else if (easting >= 2809979 && northing >= 6111691 && easting <= 2833965 && northing <= 6147660) {
            return "BL38";
        } else if (easting >= 2833961 && northing >= 6111690 && easting <= 2857942 && northing <= 6147656) {
            return "BL39";
        } else if (easting >= 2473973 && northing >= 6063734 && easting <= 2497988 && northing <= 6099750) {
            return "BM24ptBN24";
        } else if (easting >= 2497984 && northing >= 6063733 && easting <= 2521999 && northing <= 6099745) {
            return "BM25ptBN25";
        } else if (easting >= 2690022 && northing >= 6075713 && easting <= 2714023 && northing <= 6111703) {
            return "BM33";
        } else if (easting >= 2714018 && northing >= 6075711 && easting <= 2738016 && northing <= 6111699) {
            return "BM34";
        } else if (easting >= 2738012 && northing >= 6075710 && easting <= 2762006 && northing <= 6111695) {
            return "BM35";
        } else if (easting >= 2762004 && northing >= 6075710 && easting <= 2785994 && northing <= 6111692) {
            return "BM36";
        } else if (easting >= 2785993 && northing >= 6075711 && easting <= 2809979 && northing <= 6111691) {
            return "BM37";
        } else if (easting >= 2809980 && northing >= 6075713 && easting <= 2833961 && northing <= 6111690) {
            return "BM38";
        } else if (easting >= 2825970 && northing >= 6075716 && easting <= 2849948 && northing <= 6111690) {
            return "BM39ptBM38";
        } else if (easting >= 2425948 && northing >= 6039720 && easting <= 2449961 && northing <= 6075741) {
            return "BN22";
        } else if (easting >= 2449961 && northing >= 6039722 && easting <= 2473974 && northing <= 6075740) {
            return "BN23";
        } else if (easting >= 2473973 && northing >= 6039723 && easting <= 2497985 && northing <= 6075738) {
            return "BN24";
        } else if (easting >= 2497983 && northing >= 6039722 && easting <= 2521996 && northing <= 6075736) {
            return "BN25";
        } else if (easting >= 2570008 && northing >= 6039720 && easting <= 2594017 && northing <= 6075726) {
            return "BN28";
        } else if (easting >= 2586012 && northing >= 6039719 && easting <= 2610020 && northing <= 6075723) {
            return "BN29ptBN28";
        } else if (easting >= 2666021 && northing >= 6027716 && easting <= 2690021 && northing <= 6063714) {
            return "BN32ptBP32";
        } else if (easting >= 2690020 && northing >= 6039716 && easting <= 2714018 && northing <= 6075711) {
            return "BN33";
        } else if (easting >= 2714018 && northing >= 6039718 && easting <= 2738012 && northing <= 6075710) {
            return "BN34";
        } else if (easting >= 2738013 && northing >= 6039720 && easting <= 2762004 && northing <= 6075710) {
            return "BN35";
        } else if (easting >= 2762007 && northing >= 6039723 && easting <= 2785993 && northing <= 6075711) {
            return "BN36";
        } else if (easting >= 2785998 && northing >= 6039727 && easting <= 2809980 && northing <= 6075713) {
            return "BN37";
        } else if (easting >= 2801992 && northing >= 6039731 && easting <= 2825970 && northing <= 6075716) {
            return "BN38ptBN37";
        } else if (easting >= 2425952 && northing >= 6003702 && easting <= 2449961 && northing <= 6039722) {
            return "BP22";
        } else if (easting >= 2449964 && northing >= 6003705 && easting <= 2473973 && northing <= 6039723) {
            return "BP23";
        } else if (easting >= 2473974 && northing >= 6003707 && easting <= 2497983 && northing <= 6039722) {
            return "BP24";
        } else if (easting >= 2497984 && northing >= 6003708 && easting <= 2521993 && northing <= 6039722) {
            return "BP25";
        } else if (easting >= 2529995 && northing >= 6003709 && easting <= 2554003 && northing <= 6039720) {
            return "BP26ptBP27";
        } else if (easting >= 2546001 && northing >= 6003710 && easting <= 2570008 && northing <= 6039720) {
            return "BP27";
        } else if (easting >= 2570008 && northing >= 6003710 && easting <= 2594014 && northing <= 6039718) {
            return "BP28";
        } else if (easting >= 2594013 && northing >= 6003711 && easting <= 2618018 && northing <= 6039717) {
            return "BP29";
        } else if (easting >= 2618019 && northing >= 5991710 && easting <= 2642020 && northing <= 6027716) {
            return "BP30ptBQ30";
        } else if (easting >= 2642021 && northing >= 6003713 && easting <= 2666021 && northing <= 6039716) {
            return "BP31";
        } else if (easting >= 2666023 && northing >= 6003715 && easting <= 2690020 && northing <= 6039716) {
            return "BP32";
        } else if (easting >= 2690023 && northing >= 6003718 && easting <= 2714018 && northing <= 6039718) {
            return "BP33";
        } else if (easting >= 2714022 && northing >= 6003722 && easting <= 2738013 && northing <= 6039720) {
            return "BP34";
        } else if (easting >= 2738020 && northing >= 6003726 && easting <= 2762007 && northing <= 6039723) {
            return "BP35";
        } else if (easting >= 2762015 && northing >= 6003733 && easting <= 2785998 && northing <= 6039727) {
            return "BP36";
        } else if (easting >= 2409951 && northing >= 5967682 && easting <= 2433956 && northing <= 6003703) {
            return "BQ21ptBQ22";
        } else if (easting >= 2425958 && northing >= 5967685 && easting <= 2449964 && northing <= 6003705) {
            return "BQ22";
        } else if (easting >= 2449968 && northing >= 5967689 && easting <= 2473974 && northing <= 6003707) {
            return "BQ23";
        } else if (easting >= 2473977 && northing >= 5967692 && easting <= 2497984 && northing <= 6003708) {
            return "BQ24";
        } else if (easting >= 2497986 && northing >= 5967694 && easting <= 2521993 && northing <= 6003709) {
            return "BQ25";
        } else if (easting >= 2521995 && northing >= 5967697 && easting <= 2546001 && northing <= 6003710) {
            return "BQ26";
        } else if (easting >= 2546003 && northing >= 5967698 && easting <= 2570008 && northing <= 6003710) {
            return "BQ27";
        } else if (easting >= 2570010 && northing >= 5967700 && easting <= 2594013 && northing <= 6003711) {
            return "BQ28";
        } else if (easting >= 2594016 && northing >= 5967703 && easting <= 2618018 && northing <= 6003712) {
            return "BQ29";
        } else if (easting >= 2642025 && northing >= 5967709 && easting <= 2666023 && northing <= 6003715) {
            return "BQ31";
        } else if (easting >= 2666028 && northing >= 5967713 && easting <= 2690023 && northing <= 6003718) {
            return "BQ32";
        } else if (easting >= 2690030 && northing >= 5967717 && easting <= 2714022 && northing <= 6003722) {
            return "BQ33";
        } else if (easting >= 2714030 && northing >= 5967723 && easting <= 2738020 && northing <= 6003726) {
            return "BQ34";
        } else if (easting >= 2738029 && northing >= 5967730 && easting <= 2762015 && northing <= 6003733) {
            return "BQ35";
        } else if (easting >= 2754028 && northing >= 5967736 && easting <= 2778011 && northing <= 6003737) {
            return "BQ36ptBQ35";
        } else if (easting >= 2377947 && northing >= 5931661 && easting <= 2401948 && northing <= 5967681) {
            return "BR20";
        } else if (easting >= 2401955 && northing >= 5931666 && easting <= 2425958 && northing <= 5967685) {
            return "BR21";
        } else if (easting >= 2425964 && northing >= 5931671 && easting <= 2449968 && northing <= 5967689) {
            return "BR22";
        } else if (easting >= 2449973 && northing >= 5931675 && easting <= 2473977 && northing <= 5967692) {
            return "BR23";
        } else if (easting >= 2473982 && northing >= 5931679 && easting <= 2497986 && northing <= 5967694) {
            return "BR24";
        } else if (easting >= 2497990 && northing >= 5931682 && easting <= 2521995 && northing <= 5967697) {
            return "BR25";
        } else if (easting >= 2521999 && northing >= 5931684 && easting <= 2546003 && northing <= 5967698) {
            return "BR26";
        } else if (easting >= 2546006 && northing >= 5931687 && easting <= 2570010 && northing <= 5967700) {
            return "BR27";
        } else if (easting >= 2570014 && northing >= 5931690 && easting <= 2594016 && northing <= 5967703) {
            return "BR28";
        } else if (easting >= 2594020 && northing >= 5931694 && easting <= 2618021 && northing <= 5967705) {
            return "BR29";
        } else if (easting >= 2690039 && northing >= 5931714 && easting <= 2714030 && northing <= 5967723) {
            return "BR33";
        } else if (easting >= 2714042 && northing >= 5931722 && easting <= 2738029 && northing <= 5967730) {
            return "BR34";
        } else if (easting >= 2353949 && northing >= 5895643 && easting <= 2377947 && northing <= 5931661) {
            return "BS19";
        } else if (easting >= 2377956 && northing >= 5895649 && easting <= 2401955 && northing <= 5931666) {
            return "BS20";
        } else if (easting >= 2401963 && northing >= 5895654 && easting <= 2425964 && northing <= 5931671) {
            return "BS21";
        } else if (easting >= 2425971 && northing >= 5895659 && easting <= 2449973 && northing <= 5931675) {
            return "BS22";
        } else if (easting >= 2449979 && northing >= 5895663 && easting <= 2473982 && northing <= 5931679) {
            return "BS23";
        } else if (easting >= 2473987 && northing >= 5895666 && easting <= 2497990 && northing <= 5931682) {
            return "BS24";
        } else if (easting >= 2497995 && northing >= 5895669 && easting <= 2521999 && northing <= 5931684) {
            return "BS25";
        } else if (easting >= 2522003 && northing >= 5895672 && easting <= 2546006 && northing <= 5931687) {
            return "BS26";
        } else if (easting >= 2546011 && northing >= 5895676 && easting <= 2570014 && northing <= 5931690) {
            return "BS27";
        } else if (easting >= 2570019 && northing >= 5895679 && easting <= 2594020 && northing <= 5931694) {
            return "BS28";
        } else if (easting >= 2594026 && northing >= 5895684 && easting <= 2618026 && northing <= 5931698) {
            return "BS29";
        } else if (easting >= 2353958 && northing >= 5859634 && easting <= 2377956 && northing <= 5895649) {
            return "BT19";
        } else if (easting >= 2377964 && northing >= 5859639 && easting <= 2401963 && northing <= 5895654) {
            return "BT20";
        } else if (easting >= 2401970 && northing >= 5859644 && easting <= 2425971 && northing <= 5895659) {
            return "BT21";
        } else if (easting >= 2425976 && northing >= 5859648 && easting <= 2449979 && northing <= 5895663) {
            return "BT22";
        } else if (easting >= 2449984 && northing >= 5859651 && easting <= 2473987 && northing <= 5895666) {
            return "BT23";
        } else if (easting >= 2473991 && northing >= 5859654 && easting <= 2497995 && northing <= 5895669) {
            return "BT24";
        } else if (easting >= 2498000 && northing >= 5859657 && easting <= 2522003 && northing <= 5895672) {
            return "BT25";
        } else if (easting >= 2522008 && northing >= 5859660 && easting <= 2546011 && northing <= 5895676) {
            return "BT26";
        } else if (easting >= 2546017 && northing >= 5859663 && easting <= 2570019 && northing <= 5895679) {
            return "BT27";
        } else if (easting >= 2570025 && northing >= 5859667 && easting <= 2594026 && northing <= 5895684) {
            return "BT28";
        } else if (easting >= 2329963 && northing >= 5823624 && easting <= 2353958 && northing <= 5859634) {
            return "BU18";
        } else if (easting >= 2353966 && northing >= 5823628 && easting <= 2377964 && northing <= 5859639) {
            return "BU19";
        } else if (easting >= 2377970 && northing >= 5823632 && easting <= 2401970 && northing <= 5859644) {
            return "BU20";
        } else if (easting >= 2401975 && northing >= 5823635 && easting <= 2425976 && northing <= 5859648) {
            return "BU21";
        } else if (easting >= 2425981 && northing >= 5823637 && easting <= 2449984 && northing <= 5859651) {
            return "BU22";
        } else if (easting >= 2449988 && northing >= 5823640 && easting <= 2473991 && northing <= 5859654) {
            return "BU23";
        } else if (easting >= 2473995 && northing >= 5823642 && easting <= 2498000 && northing <= 5859657) {
            return "BU24";
        } else if (easting >= 2498004 && northing >= 5823644 && easting <= 2522008 && northing <= 5859660) {
            return "BU25";
        } else if (easting >= 2522013 && northing >= 5823647 && easting <= 2546017 && northing <= 5859663) {
            return "BU26";
        } else if (easting >= 2546022 && northing >= 5823650 && easting <= 2570025 && northing <= 5859667) {
            return "BU27";
        } else if (easting >= 2281972 && northing >= 5787615 && easting <= 2305962 && northing <= 5823618) {
            return "BV16";
        } else if (easting >= 2305970 && northing >= 5787619 && easting <= 2329963 && northing <= 5823624) {
            return "BV17";
        } else if (easting >= 2329969 && northing >= 5787622 && easting <= 2353966 && northing <= 5823628) {
            return "BV18";
        } else if (easting >= 2353970 && northing >= 5787624 && easting <= 2377970 && northing <= 5823632) {
            return "BV19";
        } else if (easting >= 2377973 && northing >= 5787626 && easting <= 2401975 && northing <= 5823635) {
            return "BV20";
        } else if (easting >= 2401978 && northing >= 5787627 && easting <= 2425981 && northing <= 5823637) {
            return "BV21";
        } else if (easting >= 2425983 && northing >= 5787628 && easting <= 2449988 && northing <= 5823640) {
            return "BV22";
        } else if (easting >= 2449990 && northing >= 5787629 && easting <= 2473995 && northing <= 5823642) {
            return "BV23";
        } else if (easting >= 2473998 && northing >= 5787630 && easting <= 2498004 && northing <= 5823644) {
            return "BV24";
        } else if (easting >= 2498007 && northing >= 5787631 && easting <= 2522013 && northing <= 5823647) {
            return "BV25";
        } else if (easting >= 2522016 && northing >= 5787633 && easting <= 2546022 && northing <= 5823650) {
            return "BV26";
        } else if (easting >= 2233993 && northing >= 5739621 && easting <= 2257979 && northing <= 5775612) {
            return "BW14ptBX14";
        } else if (easting >= 2257983 && northing >= 5751619 && easting <= 2281972 && northing <= 5787615) {
            return "BW15";
        } else if (easting >= 2281977 && northing >= 5751621 && easting <= 2305970 && northing <= 5787619) {
            return "BW16";
        } else if (easting >= 2305973 && northing >= 5751622 && easting <= 2329969 && northing <= 5787622) {
            return "BW17";
        } else if (easting >= 2329971 && northing >= 5751622 && easting <= 2353970 && northing <= 5787624) {
            return "BW18";
        } else if (easting >= 2353971 && northing >= 5751622 && easting <= 2377973 && northing <= 5787626) {
            return "BW19";
        } else if (easting >= 2377974 && northing >= 5751621 && easting <= 2401978 && northing <= 5787627) {
            return "BW20";
        } else if (easting >= 2401978 && northing >= 5751620 && easting <= 2425983 && northing <= 5787628) {
            return "BW21";
        } else if (easting >= 2425983 && northing >= 5751619 && easting <= 2449990 && northing <= 5787629) {
            return "BW22";
        } else if (easting >= 2449990 && northing >= 5751618 && easting <= 2473998 && northing <= 5787630) {
            return "BW23";
        } else if (easting >= 2473998 && northing >= 5751617 && easting <= 2498007 && northing <= 5787631) {
            return "BW24";
        } else if (easting >= 2490005 && northing >= 5751617 && easting <= 2514013 && northing <= 5787632) {
            return "BW25ptBW24";
        } else if (easting >= 2186026 && northing >= 5703640 && easting <= 2210006 && northing <= 5739618) {
            return "BX12ptBY12";
        } else if (easting >= 2210008 && northing >= 5715632 && easting <= 2233992 && northing <= 5751616) {
            return "BX13";
        } else if (easting >= 2233994 && northing >= 5715632 && easting <= 2257983 && northing <= 5751619) {
            return "BX14";
        } else if (easting >= 2257984 && northing >= 5715631 && easting <= 2281977 && northing <= 5751621) {
            return "BX15";
        } else if (easting >= 2281976 && northing >= 5715630 && easting <= 2305973 && northing <= 5751622) {
            return "BX16";
        } else if (easting >= 2305971 && northing >= 5715627 && easting <= 2329971 && northing <= 5751622) {
            return "BX17";
        } else if (easting >= 2329968 && northing >= 5715624 && easting <= 2353971 && northing <= 5751622) {
            return "BX18";
        } else if (easting >= 2353968 && northing >= 5715620 && easting <= 2377974 && northing <= 5751621) {
            return "BX19";
        } else if (easting >= 2377970 && northing >= 5715616 && easting <= 2401978 && northing <= 5751620) {
            return "BX20";
        } else if (easting >= 2401974 && northing >= 5715613 && easting <= 2425983 && northing <= 5751619) {
            return "BX21";
        } else if (easting >= 2425980 && northing >= 5715609 && easting <= 2449990 && northing <= 5751618) {
            return "BX22";
        } else if (easting >= 2449987 && northing >= 5715606 && easting <= 2473998 && northing <= 5751617) {
            return "BX23";
        } else if (easting >= 2473996 && northing >= 5715603 && easting <= 2498008 && northing <= 5751617) {
            return "BX24";
        } else if (easting >= 2498007 && northing >= 5715601 && easting <= 2522018 && northing <= 5751617) {
            return "BX25";
        } else if (easting >= 2138073 && northing >= 5667678 && easting <= 2162047 && northing <= 5703639) {
            return "BY10ptBZ10";
        } else if (easting >= 2162047 && northing >= 5679662 && easting <= 2186025 && northing <= 5715630) {
            return "BY11";
        } else if (easting >= 2186024 && northing >= 5679660 && easting <= 2210008 && northing <= 5715632) {
            return "BY12";
        } else if (easting >= 2210006 && northing >= 5679656 && easting <= 2233994 && northing <= 5715632) {
            return "BY13";
        } else if (easting >= 2233990 && northing >= 5679652 && easting <= 2257984 && northing <= 5715631) {
            return "BY14";
        } else if (easting >= 2257978 && northing >= 5679646 && easting <= 2281976 && northing <= 5715630) {
            return "BY15";
        } else if (easting >= 2281970 && northing >= 5679640 && easting <= 2305971 && northing <= 5715627) {
            return "BY16";
        } else if (easting >= 2305964 && northing >= 5679633 && easting <= 2329968 && northing <= 5715624) {
            return "BY17";
        } else if (easting >= 2329961 && northing >= 5679626 && easting <= 2353968 && northing <= 5715620) {
            return "BY18";
        } else if (easting >= 2353960 && northing >= 5679619 && easting <= 2377970 && northing <= 5715616) {
            return "BY19";
        } else if (easting >= 2377962 && northing >= 5679612 && easting <= 2401974 && northing <= 5715613) {
            return "BY20";
        } else if (easting >= 2401966 && northing >= 5679605 && easting <= 2425980 && northing <= 5715609) {
            return "BY21";
        } else if (easting >= 2425973 && northing >= 5679598 && easting <= 2449987 && northing <= 5715606) {
            return "BY22";
        } else if (easting >= 2449981 && northing >= 5679593 && easting <= 2473996 && northing <= 5715603) {
            return "BY23";
        } else if (easting >= 2473991 && northing >= 5679587 && easting <= 2498007 && northing <= 5715601) {
            return "BY24";
        } else if (easting >= 2498003 && northing >= 5679583 && easting <= 2522019 && northing <= 5715600) {
            return "BY25";
        } else if (easting >= 2114101 && northing >= 5643713 && easting <= 2138073 && northing <= 5679663) {
            return "BZ09";
        } else if (easting >= 2138068 && northing >= 5643707 && easting <= 2162047 && northing <= 5679662) {
            return "BZ10";
        } else if (easting >= 2162040 && northing >= 5643700 && easting <= 2186024 && northing <= 5679660) {
            return "BZ11";
        } else if (easting >= 2186016 && northing >= 5643692 && easting <= 2210006 && northing <= 5679656) {
            return "BZ12";
        } else if (easting >= 2209995 && northing >= 5643683 && easting <= 2233990 && northing <= 5679652) {
            return "BZ13";
        } else if (easting >= 2233979 && northing >= 5643673 && easting <= 2257978 && northing <= 5679646) {
            return "BZ14";
        } else if (easting >= 2257966 && northing >= 5643662 && easting <= 2281970 && northing <= 5679640) {
            return "BZ15";
        } else if (easting >= 2281957 && northing >= 5643651 && easting <= 2305964 && northing <= 5679633) {
            return "BZ16";
        } else if (easting >= 2305950 && northing >= 5643640 && easting <= 2329961 && northing <= 5679626) {
            return "BZ17";
        } else if (easting >= 2329947 && northing >= 5643629 && easting <= 2353960 && northing <= 5679619) {
            return "BZ18";
        } else if (easting >= 2353947 && northing >= 5643618 && easting <= 2377962 && northing <= 5679612) {
            return "BZ19";
        } else if (easting >= 2377949 && northing >= 5643607 && easting <= 2401966 && northing <= 5679605) {
            return "BZ20";
        } else if (easting >= 2393952 && northing >= 5643600 && easting <= 2417970 && northing <= 5679601) {
            return "BZ21ptBZ20";
        } else if (easting >= 2066165 && northing >= 5595814 && easting <= 2090136 && northing <= 5631738) {
            return "CA07ptCB07";
        } else if (easting >= 2090127 && northing >= 5607779 && easting <= 2114101 && northing <= 5643713) {
            return "CA08";
        } else if (easting >= 2114088 && northing >= 5607767 && easting <= 2138068 && northing <= 5643707) {
            return "CA09";
        } else if (easting >= 2138053 && northing >= 5607754 && easting <= 2162040 && northing <= 5643700) {
            return "CA10";
        } else if (easting >= 2162023 && northing >= 5607741 && easting <= 2186016 && northing <= 5643692) {
            return "CA11";
        } else if (easting >= 2185998 && northing >= 5607726 && easting <= 2209995 && northing <= 5643683) {
            return "CA12";
        } else if (easting >= 2209977 && northing >= 5607711 && easting <= 2233979 && northing <= 5643673) {
            return "CA13";
        } else if (easting >= 2233959 && northing >= 5607695 && easting <= 2257966 && northing <= 5643662) {
            return "CA14";
        } else if (easting >= 2257946 && northing >= 5607679 && easting <= 2281957 && northing <= 5643651) {
            return "CA15";
        } else if (easting >= 2281936 && northing >= 5607663 && easting <= 2305950 && northing <= 5643640) {
            return "CA16";
        } else if (easting >= 2305930 && northing >= 5607647 && easting <= 2329947 && northing <= 5643629) {
            return "CA17";
        } else if (easting >= 2329927 && northing >= 5607631 && easting <= 2353947 && northing <= 5643618) {
            return "CA18";
        } else if (easting >= 2353927 && northing >= 5607616 && easting <= 2377949 && northing <= 5643607) {
            return "CA19";
        } else if (easting >= 2042201 && northing >= 5571880 && easting <= 2066171 && northing <= 5607790) {
            return "CB06";
        } else if (easting >= 2066150 && northing >= 5571862 && easting <= 2090127 && northing <= 5607779) {
            return "CB07";
        } else if (easting >= 2090104 && northing >= 5571843 && easting <= 2114088 && northing <= 5607767) {
            return "CB08";
        } else if (easting >= 2114063 && northing >= 5571824 && easting <= 2138053 && northing <= 5607754) {
            return "CB09";
        } else if (easting >= 2138028 && northing >= 5571803 && easting <= 2162023 && northing <= 5607741) {
            return "CB10";
        } else if (easting >= 2161997 && northing >= 5571783 && easting <= 2185998 && northing <= 5607726) {
            return "CB11";
        } else if (easting >= 2185971 && northing >= 5571762 && easting <= 2209977 && northing <= 5607711) {
            return "CB12";
        } else if (easting >= 2209949 && northing >= 5571740 && easting <= 2233959 && northing <= 5607695) {
            return "CB13";
        } else if (easting >= 2233931 && northing >= 5571718 && easting <= 2257946 && northing <= 5607679) {
            return "CB14";
        } else if (easting >= 2257918 && northing >= 5571697 && easting <= 2281936 && northing <= 5607663) {
            return "CB15";
        } else if (easting >= 2281908 && northing >= 5571675 && easting <= 2305930 && northing <= 5607647) {
            return "CB16";
        } else if (easting >= 2305902 && northing >= 5571654 && easting <= 2329927 && northing <= 5607631) {
            return "CB17";
        } else if (easting >= 2329900 && northing >= 5571633 && easting <= 2353927 && northing <= 5607616) {
            return "CB18";
        } else if (easting >= 2353901 && northing >= 5571613 && easting <= 2377930 && northing <= 5607601) {
            return "CB19";
        } else if (easting >= 2018226 && northing >= 5535988 && easting <= 2042201 && northing <= 5571880) {
            return "CC05";
        } else if (easting >= 2042168 && northing >= 5535962 && easting <= 2066150 && northing <= 5571862) {
            return "CC06";
        } else if (easting >= 2066116 && northing >= 5535935 && easting <= 2090104 && northing <= 5571843) {
            return "CC07";
        } else if (easting >= 2090069 && northing >= 5535908 && easting <= 2114063 && northing <= 5571824) {
            return "CC08";
        } else if (easting >= 2114028 && northing >= 5535881 && easting <= 2138028 && northing <= 5571803) {
            return "CC09";
        } else if (easting >= 2137992 && northing >= 5535854 && easting <= 2161997 && northing <= 5571783) {
            return "CC10";
        } else if (easting >= 2161960 && northing >= 5535826 && easting <= 2185971 && northing <= 5571762) {
            return "CC11";
        } else if (easting >= 2185934 && northing >= 5535798 && easting <= 2209949 && northing <= 5571740) {
            return "CC12";
        } else if (easting >= 2209912 && northing >= 5535770 && easting <= 2233931 && northing <= 5571718) {
            return "CC13";
        } else if (easting >= 2233894 && northing >= 5535742 && easting <= 2257918 && northing <= 5571697) {
            return "CC14";
        } else if (easting >= 2257881 && northing >= 5535714 && easting <= 2281908 && northing <= 5571675) {
            return "CC15";
        } else if (easting >= 2281872 && northing >= 5535687 && easting <= 2305902 && northing <= 5571654) {
            return "CC16";
        } else if (easting >= 2305866 && northing >= 5535660 && easting <= 2329900 && northing <= 5571633) {
            return "CC17";
        } else if (easting >= 2329865 && northing >= 5535633 && easting <= 2353901 && northing <= 5571613) {
            return "CC18";
        } else if (easting >= 2345866 && northing >= 5535616 && easting <= 2369903 && northing <= 5571600) {
            return "CC19ptCC18";
        } else if (easting >= 2002223 && northing >= 5500103 && easting <= 2026206 && northing <= 5535979) {
            return "CD04ptCD05";
        } else if (easting >= 2018181 && northing >= 5500080 && easting <= 2042168 && northing <= 5535962) {
            return "CD05";
        } else if (easting >= 2042122 && northing >= 5500045 && easting <= 2066116 && northing <= 5535935) {
            return "CD06";
        } else if (easting >= 2066070 && northing >= 5500010 && easting <= 2090069 && northing <= 5535908) {
            return "CD07";
        } else if (easting >= 2090023 && northing >= 5499975 && easting <= 2114028 && northing <= 5535881) {
            return "CD08";
        } else if (easting >= 2113981 && northing >= 5499940 && easting <= 2137992 && northing <= 5535854) {
            return "CD09";
        } else if (easting >= 2137945 && northing >= 5499904 && easting <= 2161960 && northing <= 5535826) {
            return "CD10";
        } else if (easting >= 2161913 && northing >= 5499869 && easting <= 2185934 && northing <= 5535798) {
            return "CD11";
        } else if (easting >= 2185887 && northing >= 5499834 && easting <= 2209912 && northing <= 5535770) {
            return "CD12";
        } else if (easting >= 2209865 && northing >= 5499799 && easting <= 2233894 && northing <= 5535742) {
            return "CD13";
        } else if (easting >= 2233848 && northing >= 5499765 && easting <= 2257881 && northing <= 5535714) {
            return "CD14";
        } else if (easting >= 2257835 && northing >= 5499730 && easting <= 2281872 && northing <= 5535687) {
            return "CD15";
        } else if (easting >= 2281826 && northing >= 5499697 && easting <= 2305866 && northing <= 5535660) {
            return "CD16";
        } else if (easting >= 2305822 && northing >= 5499664 && easting <= 2329865 && northing <= 5535633) {
            return "CD17";
        } else if (easting >= 2329822 && northing >= 5499632 && easting <= 2353867 && northing <= 5535608) {
            return "CD18";
        } else if (easting >= 1994186 && northing >= 5464215 && easting <= 2018181 && northing <= 5500080) {
            return "CE04";
        } else if (easting >= 2018122 && northing >= 5464172 && easting <= 2042122 && northing <= 5500045) {
            return "CE05";
        } else if (easting >= 2042064 && northing >= 5464128 && easting <= 2066070 && northing <= 5500010) {
            return "CE06";
        } else if (easting >= 2066011 && northing >= 5464085 && easting <= 2090023 && northing <= 5499975) {
            return "CE07";
        } else if (easting >= 2089964 && northing >= 5464041 && easting <= 2113981 && northing <= 5499940) {
            return "CE08";
        } else if (easting >= 2113922 && northing >= 5463998 && easting <= 2137945 && northing <= 5499904) {
            return "CE09";
        } else if (easting >= 2137886 && northing >= 5463955 && easting <= 2161913 && northing <= 5499869) {
            return "CE10";
        } else if (easting >= 2161855 && northing >= 5463913 && easting <= 2185887 && northing <= 5499834) {
            return "CE11";
        } else if (easting >= 2185829 && northing >= 5463870 && easting <= 2209865 && northing <= 5499799) {
            return "CE12";
        } else if (easting >= 2209807 && northing >= 5463828 && easting <= 2233848 && northing <= 5499765) {
            return "CE13";
        } else if (easting >= 2233791 && northing >= 5463786 && easting <= 2257835 && northing <= 5499730) {
            return "CE14";
        } else if (easting >= 2257779 && northing >= 5463746 && easting <= 2281826 && northing <= 5499697) {
            return "CE15";
        } else if (easting >= 2281772 && northing >= 5463705 && easting <= 2305822 && northing <= 5499664) {
            return "CE16";
        } else if (easting >= 2305769 && northing >= 5463666 && easting <= 2329822 && northing <= 5499632) {
            return "CE17";
        } else if (easting >= 2329770 && northing >= 5463628 && easting <= 2353826 && northing <= 5499601) {
            return "CE18";
        } else if (easting >= 1994113 && northing >= 5428316 && easting <= 2018122 && northing <= 5464172) {
            return "CF04";
        } else if (easting >= 2018050 && northing >= 5428263 && easting <= 2042064 && northing <= 5464128) {
            return "CF05";
        } else if (easting >= 2041992 && northing >= 5428211 && easting <= 2066011 && northing <= 5464085) {
            return "CF06";
        } else if (easting >= 2065940 && northing >= 5428159 && easting <= 2089964 && northing <= 5464041) {
            return "CF07";
        } else if (easting >= 2089893 && northing >= 5428108 && easting <= 2113922 && northing <= 5463998) {
            return "CF08";
        } else if (easting >= 2113852 && northing >= 5428056 && easting <= 2137886 && northing <= 5463955) {
            return "CF09";
        } else if (easting >= 2137816 && northing >= 5428006 && easting <= 2161855 && northing <= 5463913) {
            return "CF10";
        } else if (easting >= 2161785 && northing >= 5427955 && easting <= 2185829 && northing <= 5463870) {
            return "CF11";
        } else if (easting >= 2185760 && northing >= 5427905 && easting <= 2209807 && northing <= 5463828) {
            return "CF12";
        } else if (easting >= 2209739 && northing >= 5427856 && easting <= 2233791 && northing <= 5463786) {
            return "CF13";
        } else if (easting >= 2233724 && northing >= 5427807 && easting <= 2257779 && northing <= 5463746) {
            return "CF14";
        } else if (easting >= 2257713 && northing >= 5427759 && easting <= 2281772 && northing <= 5463705) {
            return "CF15";
        } else if (easting >= 2281707 && northing >= 5427712 && easting <= 2305769 && northing <= 5463666) {
            return "CF16";
        } else if (easting >= 2017965 && northing >= 5392353 && easting <= 2041992 && northing <= 5428211) {
            return "CG05";
        } else if (easting >= 2041908 && northing >= 5392293 && easting <= 2065940 && northing <= 5428159) {
            return "CG06";
        } else if (easting >= 2065885 && northing >= 5404208 && easting <= 2089918 && northing <= 5440086) {
            return "CG07ptCF07";
        } else if (easting >= 2089810 && northing >= 5392173 && easting <= 2113852 && northing <= 5428056) {
            return "CG08";
        } else if (easting >= 2113769 && northing >= 5392114 && easting <= 2137816 && northing <= 5428006) {
            return "CG09";
        } else if (easting >= 2137734 && northing >= 5392055 && easting <= 2161785 && northing <= 5427955) {
            return "CG10";
        } else if (easting >= 2161704 && northing >= 5391997 && easting <= 2185760 && northing <= 5427905) {
            return "CG11";
        } else if (easting >= 2185679 && northing >= 5391939 && easting <= 2209739 && northing <= 5427856) {
            return "CG12";
        } else if (easting >= 2209660 && northing >= 5391882 && easting <= 2233724 && northing <= 5427807) {
            return "CG13";
        } else if (easting >= 2233645 && northing >= 5391826 && easting <= 2257713 && northing <= 5427759) {
            return "CG14";
        } else if (easting >= 2257636 && northing >= 5391770 && easting <= 2281707 && northing <= 5427712) {
            return "CG15";
        } else if (easting >= 2029838 && northing >= 5356408 && easting <= 2053881 && northing <= 5392263) {
            return "CH05/CH06";
        } else if (easting >= 2089715 && northing >= 5356238 && easting <= 2113769 && northing <= 5392114) {
            return "CH08";
        } else if (easting >= 2113675 && northing >= 5356170 && easting <= 2137734 && northing <= 5392055) {
            return "CH09";
        } else if (easting >= 2137640 && northing >= 5356104 && easting <= 2161704 && northing <= 5391997) {
            return "CH10";
        } else if (easting >= 2161611 && northing >= 5356037 && easting <= 2185679 && northing <= 5391939) {
            return "CH11";
        } else if (easting >= 2185587 && northing >= 5355972 && easting <= 2209660 && northing <= 5391882) {
            return "CH12";
        } else if (easting >= 2209569 && northing >= 5355907 && easting <= 2233645 && northing <= 5391826) {
            return "CH13";
        } else if (easting >= 2065594 && northing >= 5302412 && easting <= 2089663 && northing <= 5338270) {
            return "CJ07/CK07";
        } else if (easting >= 2089608 && northing >= 5320301 && easting <= 2113675 && northing <= 5356170) {
            return "CJ08";
        } else if (easting >= 2113568 && northing >= 5320226 && easting <= 2137640 && northing <= 5356104) {
            return "CJ09";
        } else if (easting >= 2137534 && northing >= 5320151 && easting <= 2161611 && northing <= 5356037) {
            return "CJ10";
        } else if (easting >= 2089489 && northing >= 5284364 && easting <= 2113568 && northing <= 5320226) {
            return "CK08";
        } else if (easting >= 4381264 && northing >= 5740514 && easting <= 4401819 && northing <= 5779917) {
            return "CI01";
        } else if (easting >= 4405880 && northing >= 5743147 && easting <= 4426212 && northing <= 5782703) {
            return "CI02";
        } else if (easting >= 4430503 && northing >= 5745931 && easting <= 4450600 && northing <= 5785644) {
            return "CI03";
        } else if (easting >= 4409925 && northing >= 5706036 && easting <= 4430503 && northing <= 5745931) {
            return "CI04";
        } else if (easting >= 4434781 && northing >= 5708803 && easting <= 4455130 && northing <= 5748873) {
            return "CI05";
        } else if (easting >= 4439025 && northing >= 5671314 && easting <= 4459652 && northing <= 5711731) {
            return "CI06";
        } else {
            return null;
        }
    }

    /**
     * NZTM
     *
     * @return EPSG code
     */
    public int getDefaultEPSG() {
        return 2193;
    }
    
    @Override
     public int getMapsheetLookupEPSG() {
        return 27200;
    }
    
}
