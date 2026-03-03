package nz.cri.gns.newsite.utils;

import nz.cri.gns.newsite.model.MapSheetPayload;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QmapSheetTest {

    @Test
    public void testSingletonInstance() {
        QMAPSheet instance1 = QMAPSheet.getInstance();
        QMAPSheet instance2 = QMAPSheet.getInstance();

        // Assert that both instances are the same
        assertSame(instance1, instance2);
    }

    @Test
    public void testLookupBoundingBox() {
        QMAPSheet sheet = QMAPSheet.getInstance();

        Geometry bbox = sheet.getBoundingBox("Auckland");

        // Validate bounding box dimensions for the Auckland map sheet
        assertNotNull(bbox);
        assertTrue(bbox instanceof Polygon);
    }

    @Test
    public void testBoundingBoxGeoJson() {
        QMAPSheet sheet = QMAPSheet.getInstance();

        String geoJson = sheet.getBoundingBoxGeoJson("Wellington");

        // Validate GeoJSON representation
        assertNotNull(geoJson);
        assertTrue(geoJson.contains("coordinates"));
    }

    @Ignore
    public void testLookupMapSheetByCoordinates() {
        QMAPSheet sheet = QMAPSheet.getInstance();

        String sheetName = sheet.lookupMapSheet(2570000, 6550000); // Auckland coordinates

        assertEquals("Auckland", sheetName);
    }

    @Test
    public void testDefaultEPSGCode() {
        assertEquals(27200, QMAPSheet.getDefaultEPSG());
        assertEquals(27200, QMAPSheet.getInstance().getMapsheetLookupEPSG());
    }

    @Test
    public void testGetAllMapSheets() {
        List<MapSheetPayload> sheets = QMAPSheet.getInstance().getAllSheets();

        // Validate the non-empty list of map sheets
        assertNotNull(sheets);
        assertFalse(sheets.isEmpty());
    }

}

