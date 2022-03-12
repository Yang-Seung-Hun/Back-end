package com.hanghae99.boilerplate.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.dto.responseDto.ResponseDto;
import com.hanghae99.boilerplate.service.SignupLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"회원가입 & 로그인"})
@Slf4j
@RestController
public class SignupLoginController {

    @Autowired
    SignupLoginService signupLoginService;
    @Autowired
    ObjectMapper objectMapper;

    @ApiOperation(value="회원가입 요청")
    @PostMapping("/api/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupReqestDto signupReqest ){
        try {
            signupLoginService.signupRequest(signupReqest);
        }
        catch (Exception e){
           return ResponseEntity.ok().body(new ResponseDto(HttpStatus.BAD_REQUEST,e.getMessage()));
        }
        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK,"signup success"));

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
