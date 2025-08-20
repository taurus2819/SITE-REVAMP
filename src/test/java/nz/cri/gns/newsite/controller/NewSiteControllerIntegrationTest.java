package nz.cri.gns.newsite.controller;

import com.fasterxml.jackson.databind.JsonNode;
import nz.cri.gns.newsite.model.SiteModelInput;
import nz.cri.gns.newsite.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest  // loads the full app context
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NewSiteControllerIntegrationTest extends BaseNewSiteControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
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
        String inputSiteModel = objectMapper.writeValueAsString(input);
        JsonNode node = objectMapper.readTree(inputSiteModel);

        // Mock the service save method
        //[SiteModel@485a5c9 id = [null], site_name = 'test', lat = -39.376384849016404, lon = 176.3281475819097, OrigSysId = 16, OrigCoord = {"epsg":27200,"format":"gridRef","gridReference":"U20/967978"}, User = [null]]
//        when(newSiteService.insert(any(SiteModel.class))).thenReturn(output);

        mockMvc.perform(post("/site/api/v1/site")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputSiteModel))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("siteName").value("controller_integration_test")); // Hibernate assigns real ID
    }
}