/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author sitikond
 */
class ProcessOrigCoord {
    private static int epsg;
    private static String format;
    private static JSONArray coord;

    public ProcessOrigCoord(int epsg, String format, JSONArray coord){
        this.epsg = epsg;
        this.format = format;
        this.coord = coord;
    }
    
    public static int getEpsg() {
        return epsg;
    }

    private static void setEpsg(int epsg) {
        ProcessOrigCoord.epsg = epsg;
    }

    public static String getFormat() {
        return format;
    }

    private static void setFormat(String format) {
        ProcessOrigCoord.format = format;
    }

    public static JSONArray getCoord() {
        return coord;
    }

    private static void setCoord(JSONArray coord) {
        ProcessOrigCoord.coord = coord;
    }    
       
    public static void extractSiteInfo(JSONObject origCoord){
        //extract values from "{"epsg":4167,"format":"DD","coord":["-44.7","177.73"]}" 
        setEpsg(origCoord.getInt("epsg"));
        setFormat(origCoord.getString("format"));
        setCoord(origCoord.getJSONArray("coord"));
        
        
    }
    
}
