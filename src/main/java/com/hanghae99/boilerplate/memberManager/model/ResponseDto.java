package com.hanghae99.boilerplate.memberManager.model;

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
    private final Object data;


    public  static com.hanghae99.boilerplate.memberManager.model.ResponseDto of(HttpStatus status , final String message,Object object){
        return new com.hanghae99.boilerplate.memberManager.model.ResponseDto(status,message,object);
    }

}
