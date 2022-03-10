package com.hanghae99.boilerplate.validator;


import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ValidException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e,
                                                          HttpServletRequest reques){

        ExceptionResponse errorResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST,e.getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }




}
