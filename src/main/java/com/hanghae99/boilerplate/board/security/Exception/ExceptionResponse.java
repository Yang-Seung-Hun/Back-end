package com.hanghae99.boilerplate.board.security.Exception;


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


    public  static com.hanghae99.boilerplate.security.Exception.ExceptionResponse of(HttpStatus status , final String message){
        return new com.hanghae99.boilerplate.security.Exception.ExceptionResponse(status,message);
    }

}
