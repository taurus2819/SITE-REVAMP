/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import org.gbif.common.parsers.geospatial.LatLng;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author scaddenp
 */
@SpringBootTest
public class OrigCoordTest {

    @Test
    public void testGetJsonString() {
        assert (OrigCoord.getJsonString(75, "2696700|5953800") == null);
        System.out.println(OrigCoord.getJsonString(38, "2696700|5953800"));
        System.out.println(OrigCoord.getJsonString(16, "D50|9610|1630"));
        System.out.println(OrigCoord.getJsonString(16, "C40|8612|5642"));
        System.out.println(OrigCoord.getJsonString(28, "-39.08102037777|174.52428614166"));
        assert (OrigCoord.getJsonString(38, "2696700|5953800").equals("{\"epsg\":27200, \"format\":\"EN\", \"easting\":2696700, \"northing\":5953800}"));
        assert (OrigCoord.getJsonString(16, "D50|9610|1630").equals("{\"epsg\":27200, \"format\":\"gridref\", \"gridReference\":\"D50/961163\"}"));
        assert (OrigCoord.getJsonString(16, "C40|8612|5642").equals("{\"epsg\":27200, \"format\":\"gridref\", \"gridReference\":\"C40/86125642\"}"));
        assert (OrigCoord.getJsonString(28, "-39.08102037777|174.52428614166").equals("{\"epsg\":4167, \"format\":\"DD\", \"latitude\":\"-39.08102037777\", \"longitude\":\"174.52428614166\"}"));
    }

    private void assertDMS(String lat, String lon, double eLat, double eLon) {
        ProjCoordinate ll = OrigCoord.parseLatLng(lat, lon);
        assertEquals(eLat, ll.y, 0.000001);
        assertEquals(eLon, ll.x, 0.000001);
    }

    @Test
    public void testParseLatLng() {
        assertDMS("2°49'N", "131°47'E", 2.816667d, 131.783333d);

        assertDMS("02° 49' 52\" N", "131° 47' 03\" E", 2.831111d, 131.784167d);
        assertDMS("2°49'52\"S", "131°47'03\" W", -2.831111d, -131.784167d);
        assertDMS("2°49'52\"  n", "131°47'03\"  O", 2.831111d, 131.784167d);
        assertDMS("002°49'52\"N", "131°47'03\"E", 2.831111d, 131.784167d);
        assertDMS("2°49'N", "131°47'E", 2.816667d, 131.783333d);
        assertDMS("002°49'52''N", "131°47'03''E", 2.831111d, 131.784167d);
        assertDMS("6º39'36\"S", "35º59'59\"W", -6.66d, -35.999722);
        assertDMS("17  02.877 N", "121  05.966 E", 17.04795, 121.099433);
        assertDMS("08º37'S", "37º10'W", -8.616667, -37.166667);
        assertDMS("39g30mS", "56g27mW", -39.5, -56.45);
        assertDMS("42g24m50.00sS", "64g17m20.00sW", -42.413889, -64.288889);
        assertDMS("42º30´S", "54º14´W", -42.5, -54.233333);
        assertDMS("61o50'N", "30o45'E", 61.833333, 30.75);
        assertDMS("07°35N", "38°44E", 7.583333, 38.733333);
        assertDMS("29º32.3’N", "113º34.9’W", 29.538333, -113.581667);
        assertDMS("5°45′30″N", "100º30′30″W", 5.758333, -100.508333);
        assertDMS("13.1939 N", "59.5432 W", 13.1939, -59.5432);
        assertDMS("24 06.363 N", "110 11.969 E", 24.10605d, 110.199483d);
    }

    @Test
    public void testToWGS() {
        ProjCoordinate inputPt = new ProjCoordinate();
        inputPt.x = 2939247.5;
        inputPt.y = 6752871.3;
        ProjCoordinate outputPt;
        outputPt = OrigCoord.toWGS84(27200, inputPt);
        assertEquals(outputPt.x,177.65067414,0.00001);
        assertEquals(outputPt.y, -34.32524702,0.00001);
        inputPt.x = 172.03277;
        inputPt.y = -41.82246;
        outputPt = OrigCoord.toWGS84(4272, inputPt);
        assertEquals(outputPt.x,172.03292203,0.00005);
        assertEquals(outputPt.y, -41.82072457,0.00005);
    }

}
