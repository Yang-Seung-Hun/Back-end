package com.hanghae99.boilerplate.controller;


import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.service.SignupLoginService;
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

@RestController
public class SignupLoginController {

    @Autowired
    SignupLoginService signupLoginService;

    @PostMapping("/api/signup")
    public ResponseEntity signup(@Valid @RequestBody SignupReqestDto signupReqest, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }
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
