package nz.cri.gns.newsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *Exception to trigger when the input lat/lon strings cannot be interpreted into 
 * lat/long decimal degrees.
 * @author scaddenp
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidLatLonFormat extends  RuntimeException{

    public InvalidLatLonFormat(String message) {
        super(message);
    }    
    
}
