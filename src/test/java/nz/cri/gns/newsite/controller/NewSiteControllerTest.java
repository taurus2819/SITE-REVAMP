package nz.cri.gns.newsite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.cri.gns.newsite.audits.AuditLog;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.model.SiteModelInput;
import nz.cri.gns.newsite.model.SiteUsage;
import nz.cri.gns.newsite.service.AuditLogService;
import nz.cri.gns.newsite.service.NewSiteService;
import nz.cri.gns.newsite.service.SiteUsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@ExtendWith(MockitoExtension.class)
public class NewSiteControllerTest {

    @Mock
    private NewSiteService newSiteService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private SiteUsageService siteUsageService;

    @InjectMocks
    private NewSiteController siteController; // Assuming your controller class name

    @Mock
    private HttpServletResponse response;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should successfully create a new site with all associated records")
    void addSite_Success() throws Exception {
        // Given
        SiteModel siteCreated = createValidSiteInput();
//        SiteModel expectedSite = createExpectedSiteModel();
        Integer testSiteIdCreated = siteCreated.getSiteId();

        when(newSiteService.insert(any(SiteModel.class))).thenReturn(siteCreated);
        when(auditLogService.insert(any(AuditLog.class))).thenReturn(new AuditLog());
        when(siteUsageService.registerUsage(any(SiteUsage.class))).thenReturn(new SiteUsage());

        // Then
        assertThat(siteCreated, notNullValue());

        // Verify service interactions
        verify(newSiteService).insert(any(SiteModel.class));
        verify(auditLogService).insert(any(AuditLog.class));
        verify(siteUsageService).registerUsage(any(SiteUsage.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        // Verify audit log was added to site
        assertEquals(siteCreated.getAuditLogs().size(), 1);
    }

//    @Test
//    @DisplayName("Should handle InvalidLatLonFormat exception")
//    void addSite_InvalidLatLonFormat() throws Exception {
//        // Given
//        SiteModelInput siteInput = createInvalidLatLonSiteInput();
//
//        when(newSiteService.insert(any(SiteModel.class)))
//                .thenThrow(new InvalidLatLonFormat("Invalid latitude/longitude format"));
//
//        // When & Then
//        assertThatThrownBy(() -> siteController.addSite(siteInput, response))
//                .isInstanceOf(InvalidLatLonFormat.class)
//                .hasMessageContaining("Invalid latitude/longitude format");
//
//        verify(auditLogService, never()).insert(any());
//        verify(siteUsageService, never()).registerUsage(any());
//    }
//
//    @Test
//    @DisplayName("Should handle InvalidOrigCoordinate exception")
//    void addSite_InvalidOrigCoordinate() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//
//        when(newSiteService.insert(any(SiteModel.class)))
//                .thenThrow(new InvalidOrigCoordinate("Invalid original coordinate"));
//
//        // When & Then
//        assertThatThrownBy(() -> siteController.addSite(siteInput, response))
//                .isInstanceOf(InvalidOrigCoordinate.class);
//
//        verify(auditLogService, never()).insert(any());
//        verify(siteUsageService, never()).registerUsage(any());
//    }
//
//    @Test
//    @DisplayName("Should handle FactoryException")
//    void addSite_FactoryException() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//
//        when(newSiteService.insert(any(SiteModel.class)))
//                .thenThrow(new FactoryException("Factory error"));
//
//        // When & Then
//        assertThatThrownBy(() -> siteController.addSite(siteInput, response))
//                .isInstanceOf(FactoryException.class);
//    }
//
//    @Test
//    @DisplayName("Should handle audit log service failure gracefully")
//    void addSite_AuditLogServiceFailure() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//        SiteModel expectedSite = createExpectedSiteModel();
//        expectedSite.setSiteId(123);
//
//        when(newSiteService.insert(any(SiteModel.class))).thenReturn(expectedSite);
//        when(auditLogService.insert(any(AuditLog.class)))
//                .thenThrow(new RuntimeException("Audit log service error"));
//
//        // When & Then
//        assertThatThrownBy(() -> siteController.addSite(siteInput, response))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Audit log service error");
//
//        verify(newSiteService).insert(any(SiteModel.class));
//        verify(siteUsageService, never()).registerUsage(any());
//    }
//
//    @Test
//    @DisplayName("Should handle site usage service failure")
//    void addSite_SiteUsageServiceFailure() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//        SiteModel expectedSite = createExpectedSiteModel();
//        expectedSite.setSiteId(123);
//
//        when(newSiteService.insert(any(SiteModel.class))).thenReturn(expectedSite);
//        when(auditLogService.insert(any(AuditLog.class))).thenReturn(new AuditLog());
//        when(siteUsageService.registerUsage(any(SiteUsage.class)))
//                .thenThrow(new RuntimeException("Site usage service error"));
//
//        // When & Then
//        assertThatThrownBy(() -> siteController.addSite(siteInput, response))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Site usage service error");
//
//        verify(newSiteService).insert(any(SiteModel.class));
//        verify(auditLogService).insert(any(AuditLog.class));
//    }
//
//    @Test
//    @DisplayName("Should verify audit log contains correct message")
//    void addSite_AuditLogContent() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//        SiteModel expectedSite = createExpectedSiteModel();
//        expectedSite.setSiteId(123);
//
//        ArgumentCaptor<AuditLog> auditLogCaptor = ArgumentCaptor.forClass(AuditLog.class);
//
//        when(newSiteService.insert(any(SiteModel.class))).thenReturn(expectedSite);
//        when(auditLogService.insert(auditLogCaptor.capture())).thenReturn(new AuditLog());
//        when(siteUsageService.registerUsage(any(SiteUsage.class))).thenReturn(new SiteUsage());
//
//        // When
//        siteController.addSite(siteInput, response);
//
//        // Then
//        AuditLog capturedAuditLog = auditLogCaptor.getValue();
//        // Add assertions based on your AuditLog structure
//        // For example: assertThat(capturedAuditLog.getMessage()).contains("Newly Created");
//    }
//
//    @Test
//    @DisplayName("Should verify site usage contains correct client user")
//    void addSite_SiteUsageContent() throws Exception {
//        // Given
//        SiteModelInput siteInput = createValidSiteInput();
//        siteInput.setClientUser("testUser123");
//        SiteModel expectedSite = createExpectedSiteModel();
//        expectedSite.setSiteId(123);
//
//        ArgumentCaptor<SiteUsage> siteUsageCaptor = ArgumentCaptor.forClass(SiteUsage.class);
//
//        when(newSiteService.insert(any(SiteModel.class))).thenReturn(expectedSite);
//        when(auditLogService.insert(any(AuditLog.class))).thenReturn(new AuditLog());
//        when(siteUsageService.registerUsage(siteUsageCaptor.capture())).thenReturn(new SiteUsage());
//
//        // When
//        siteController.addSite(siteInput, response);
//
//        // Then
//        SiteUsage capturedSiteUsage = siteUsageCaptor.getValue();
//        assertThat(capturedSiteUsage.getSiteId()).isEqualTo(123);
//        // Add assertion for client user based on your SiteUsage structure
//        // For example: assertThat(capturedSiteUsage.getClientUser()).isEqualTo("testUser123");
//    }

    // Helper methods for test data creation
    private SiteModel createValidSiteInput() {
        SiteModel siteCreated = null;
        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere",
                null, null, 3.5, "NZ", "testing",1618,
                27200, "U20/967978", null, null, null, null,"GridRef", "Unit test", "FRED.FEATURE");

        // Set other required fields based on your SiteModelInput structure
        //send this SiteModelInput through the Site API addSite to crete a site
        //and then return a SiteModel
        try {
            siteCreated = siteController.addSite(smi, response);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        } catch (TransformException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return siteCreated;
    }

    private SiteModelInput createInvalidLatLonSiteInput() {
        SiteModelInput input = new SiteModelInput();
        input.setClientUser("testUser");
        // Set invalid lat/lon values that would trigger InvalidLatLonFormat
        return input;
    }

    private SiteModel createExpectedSiteModel() {
        SiteModel site = new SiteModel();
        // Set expected values based on your SiteModel structure
        return site;
    }
}
