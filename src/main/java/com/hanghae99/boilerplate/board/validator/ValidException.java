package com.hanghae99.boilerplate.board.validator;


import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ValidException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e,
                                                          HttpServletRequest reques){
        log.info(e.getMessage());
        ExceptionResponse errorResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST,e.getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }




}
