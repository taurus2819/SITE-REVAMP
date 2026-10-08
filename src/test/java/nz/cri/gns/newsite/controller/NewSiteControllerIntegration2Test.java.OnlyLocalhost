package nz.cri.gns.newsite.controller;

import org.junit.jupiter.api.Test;

import nz.cri.gns.newsite.model.SiteModelInput;
import org.springframework.test.web.servlet.client.RestTestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewSiteControllerIntegration2Test extends BaseNewSiteControllerTest{
    
    @LocalServerPort
    private int port;


    @Test
    void addSite_withRealDb_assignsGeneratedId() throws Exception {
        RestTestClient client = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        SiteModelInput input = createValidSiteInput();
        ObjectMapper objectMapper = new ObjectMapper();
        String inputSiteModel = objectMapper.writeValueAsString(input);
        
        client.post()
        .uri("/site/api/v1/site")
        .contentType(MediaType.APPLICATION_JSON) 
		//.body(inputSiteModel)
        .body(input)
		.exchange()
		.expectStatus().isCreated()
		.expectBody(Void.class);

    }
    

}