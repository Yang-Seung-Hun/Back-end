package com.hanghae99.boilerplate.signupLogin.Controller;


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


    @PostMapping("/api/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupReqestDto signupReqest ){

        signupLoginService.signupRequest(signupReqest);

        return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK,"signup success"));

    }



    @PostMapping("/api/logout")
    public void  logout (HttpServletRequest request, HttpServletResponse response) throws IOException {

        signupLoginService.logoutRequest(request);
    }





}
