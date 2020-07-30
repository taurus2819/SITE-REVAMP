/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.exception;

/**
 * Exception to trigger when error are found in the original coordinate components of
 * an input record.
 * @author scaddenp
 */
public class InvalidOrigCoordinate  extends  RuntimeException{

    public InvalidOrigCoordinate(String message) {
        super(message);
    }
    
}
