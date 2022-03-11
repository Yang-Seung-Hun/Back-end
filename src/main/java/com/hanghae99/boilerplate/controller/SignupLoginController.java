package com.hanghae99.boilerplate.controller;


import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.service.SignupLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
@Api(tags = {"회원가입 & 로그인"})
@Log4j2
@RestController
public class SignupLoginController {

    @Autowired
    SignupLoginService signupLoginService;


    @ApiOperation(value="회원가입 요청")
    @PostMapping("/api/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupReqestDto signupReqest) {
        signupLoginService.signupRequest(signupReqest);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/logout/{email}")
    public void  logout (HttpServletRequest request, HttpServletResponse response,@PathVariable String email) throws IOException {

        if(email==null|| email.isBlank() ){
            response.setStatus(HttpStatus.OK.value());
            return;
        }
        signupLoginService.logoutRequest(request,response,email);

    }





}
