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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest  // loads the full app context
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewSiteControllerIntegrationTest extends BaseNewSiteControllerTest{

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

//    @Autowired
//    private ObjectMapper objectMapper;

//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }

    @Test
    void addSite_withRealDb_assignsGeneratedId() throws Exception {
        SiteModelInput input = createValidSiteInput();

        mockMvc.perform(post("/site/api/v1/site")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(jsonPath("$.siteName").value("test")); // Hibernate assigns real ID
    }
}