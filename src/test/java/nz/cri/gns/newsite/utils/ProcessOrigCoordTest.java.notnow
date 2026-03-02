/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author sitikond
 */
@SpringBootTest
public class ProcessOrigCoordTest {
    
    @Test
    public void processOrigCoordJson() throws JSONException{
        String origCoord = "{\"epsg\":4167,\"format\":\"DD\",\"coord\":[\"-44.7\",\"177.73\"]}";

        JSONObject jsonOrigCoord = new JSONObject(origCoord);
        ProcessOrigCoord.extractSiteInfo(jsonOrigCoord);
        System.out.println("EPSG = " + ProcessOrigCoord.getEpsg());
        System.out.println("format = " + ProcessOrigCoord.getFormat());
        System.out.println("coord = " + ProcessOrigCoord.getCoord());
        
    }
    
}
