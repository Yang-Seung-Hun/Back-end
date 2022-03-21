package com.hanghae99.boilerplate.signupLogin.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.signupLogin.dto.responseDto.ResponseDto;
import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@Slf4j
@RestController
public class SignupLoginController {

    @Autowired
    SignupLoginService signupLoginService;
    @Autowired
    ObjectMapper objectMapper;
    @PostMapping("/api/signup")
    public void  signup(HttpServletRequest request,HttpServletResponse response,@Valid @RequestBody SignupReqestDto signupReqest ) throws IOException {

        signupLoginService.signupRequest(signupReqest);

        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK,"signup success"));


    }



    @PostMapping("/api/logout")
    public void  logout (HttpServletRequest request, HttpServletResponse response) throws IOException {

        signupLoginService.logoutRequest(request);
    }





}
