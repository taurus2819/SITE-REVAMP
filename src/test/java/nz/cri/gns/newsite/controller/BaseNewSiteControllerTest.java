package nz.cri.gns.newsite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.cri.gns.newsite.model.SiteModelInput;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNewSiteControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }

    protected SiteModelInput createValidSiteInput() {
//        SiteModelInput smi = new SiteModelInput("controller_integration_test",3,null,"somewhere",
//                null, null, 3.5, "NZ", "testing",1618,
//                27200, "U20/967978", null, null, null, null,"GridRef", "Unit test", "FRED.FEATURE");

        //testing for null lat/lng values
        SiteModelInput smi = new SiteModelInput("null island",3,null,"Directionless site",
                null, null, 3.5, null, "testing for a null site",1618,
                null, null, null, null, null, null,null, "Unit test for null sites", "FRED.FEATURE");

        return smi;
    }
}
