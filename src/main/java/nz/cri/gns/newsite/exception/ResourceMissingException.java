package nz.cri.gns.newsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceMissingException extends RuntimeException {
	
    public ResourceMissingException(String message) {
        super(message);
    }

    public ResourceMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
