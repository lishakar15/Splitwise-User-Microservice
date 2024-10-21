package com.splitwise.microservices.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex)
    {
     ErrorResponse errorResponse = ErrorResponse.builder()
             .status(HttpStatus.BAD_REQUEST.value())
             .message(ex.getMessage())
             .build();
        return  new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
