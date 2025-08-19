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
        SiteModelInput smi = new SiteModelInput("test",3,null,"somewhere",
                null, null, 3.5, "NZ", "testing",1618,
                27200, "U20/967978", null, null, null, null,"GridRef", "Unit test", "FRED.FEATURE");
        return smi;
    }
}
