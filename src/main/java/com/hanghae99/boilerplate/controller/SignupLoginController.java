package com.hanghae99.boilerplate.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.service.SignupLoginService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
//@Api(tags = {"회원가입 & 로그인"})
@Slf4j
@RestController
public class SignupLoginController {

    @Autowired
    SignupLoginService signupLoginService;
    @Autowired
    ObjectMapper objectMapper;

//    @ApiOperation(value="회원가입 요청")
    @PostMapping("/api/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupReqestDto signupReqest) {
        log.info("request signup!");
        try {
            signupLoginService.signupRequest(signupReqest);
        }catch (Exception e){
                ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage());
                ResponseEntity.badRequest().body(exceptionResponse);

        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/logout/{email}")
    public void  logout (HttpServletRequest request, HttpServletResponse response,@PathVariable String email) throws IOException {
        if(email==null|| email.isBlank() ){
            log.info("{email} is empty ");
            response.setStatus(HttpStatus.OK.value());
            return;
        }
        signupLoginService.logoutRequest(request,response,email);
    }





}
