package com.hanghae99.boilerplate.signupLogin.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
public class ResponseDto {

    private final HttpStatus status;

    private final String message;


    public  static ResponseDto of(HttpStatus status , final String message){
        return new ResponseDto(status,message);
    }

}
