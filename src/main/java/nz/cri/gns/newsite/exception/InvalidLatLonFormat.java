/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.exception;

/**
 *Exception to trigger when the input lat/lon strings cannot be interpreted into 
 * lat/long decimal degrees.
 * @author scaddenp
 */
public class InvalidLatLonFormat extends  RuntimeException{

    public InvalidLatLonFormat(String message) {
        super(message);
    }    
    
}
