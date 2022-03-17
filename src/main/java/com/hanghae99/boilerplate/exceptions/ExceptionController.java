package com.hanghae99.boilerplate.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.MessagingException;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;

@RestControllerAdvice
public class ExceptionController {


    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity unAuthenticationException(AuthenticationException e){
        return   ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
                new ExceptionResponse(HttpStatus.UNAUTHORIZED,e.getMessage()));

    }


    @ExceptionHandler({MessagingException.class, IOException.class})
    public ResponseEntity ServerException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,IllegalArgumentException.class})
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage()));
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED,e.getMessage()));
    }
}
