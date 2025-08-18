package nz.cri.gns.newsite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.model.Island;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteModelInput;
import nz.cri.gns.newsite.model.SiteUsage;
import nz.cri.gns.newsite.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewSiteController.class)
public class NewSiteControllerIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewSiteService newSiteService;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private SiteUsageService siteUsageService;

    @MockBean
    private IslandService islandService;

    @MockBean
    private QMAPService qmapService;

    @MockBean
    private MethodService methodService;

    @MockBean
    private MasterFileService masterFileService;

    @Autowired
    private ObjectMapper objectMapper;

//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }

    @Test
    @DisplayName("Should return 201 CREATED when site is successfully added")
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void addSite_ReturnsCreated() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
        // Given
        SiteModelInput siteCreated = createValidSiteInput();
        System.out.println("Class being serialized: " + siteCreated.getClass().getName());
        System.out.println("JSON being sent: " + objectMapper.writeValueAsString(siteCreated));

        when(newSiteService.insert(any(SiteModel.class))).thenReturn(siteCreated.toSiteModel());
//        when(newSiteService.insert(any(SiteModel.class))).thenAnswer(inv -> {
//            SiteModel s = inv.getArgument(0);
//            s.getSiteId(123);  // simulate DB assigning ID
//            return s;
//        });
        when(auditLogService.insert(any(AuditLog.class))).thenReturn(new AuditLog());
        when(siteUsageService.registerUsage(any(SiteUsage.class))).thenReturn(new SiteUsage());
//        when(islandService.findByLocation(any(Point.class)));
//        when(qmapService.findAll());

        // When & Then
        mockMvc.perform(post("/site/api/v1/site")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(siteCreated)))
                        .andDo(print());
    }

//    @Test
//    @DisplayName("Should return 400 BAD REQUEST for invalid input")
//    void addSite_InvalidInput_ReturnsBadRequest() throws Exception {
//        // Given
//        SiteModelInput invalidInput = new SiteModelInput(); // Empty/invalid input
//
//        when(newSiteService.insert(any(SiteModel.class)))
//                .thenThrow(new InvalidLatLonFormat("Invalid input"));
//
//        // When & Then
//        mockMvc.perform(post("/site")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidInput)))
//                .andExpect(status().isBadRequest());
//    }

    private SiteModelInput createValidSiteInput() {
        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere",
                null, null, 3.5, "NZ", "testing",1618,
                27200, "U20/967978", null, null, null, null,"GridRef", "Unit test", "FRED.FEATURE");
        return smi;
    }

    private SiteModel createExpectedSiteModel() {
        SiteModel site = new SiteModel();
        // Set expected values
        return site;
    }
}