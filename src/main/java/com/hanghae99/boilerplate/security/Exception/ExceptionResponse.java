package com.hanghae99.boilerplate.security.Exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {

    private final HttpStatus status;

    private final String message;


    public  static ExceptionResponse of( HttpStatus status , final String message){
        return new ExceptionResponse(status,message);
    }

}
