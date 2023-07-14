package pl.mpietrewicz.sp.app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.NoResultException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler 
  extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class, NoResultException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException e, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(e, bodyOfResponse,
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}