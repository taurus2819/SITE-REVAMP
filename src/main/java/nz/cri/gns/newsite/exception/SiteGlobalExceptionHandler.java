/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.newsite.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author sitikond
 */
@ControllerAdvice
public class SiteGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
	@ExceptionHandler(ResourceMissingException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceMissingException ex, WebRequest request) {
		SiteErrorDetails errorDetails = new SiteErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
        
        @ExceptionHandler(InvalidOrigCoordinate.class)
	public ResponseEntity<?> invalidOrigCoordException(InvalidOrigCoordinate ex, WebRequest request) {
		SiteErrorDetails errorDetails = new SiteErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
		SiteErrorDetails errorDetails = new SiteErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    
}
