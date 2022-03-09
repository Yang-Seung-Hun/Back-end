package com.hanghae99.boilerplate.controller;


import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.service.SignupLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.http.HttpResponse;

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

    @GetMapping("/api/logout/{email}")
    public void  logout (HttpServletRequest request, HttpServletResponse response,@PathVariable String email) throws IOException {
            signupLoginService.logoutRequest(request,response,email);

    }



}
