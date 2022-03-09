package com.hanghae99.boilerplate.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Controller
public class SignupException {

    @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity illegalArgumentException(IllegalArgumentException e){
        String errors = e.getMessage();
        e.getStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
