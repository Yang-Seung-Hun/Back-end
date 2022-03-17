package com.hanghae99.boilerplate.memberManager.mail.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import com.hanghae99.boilerplate.memberManager.mail.FindPasswordDto;
import com.hanghae99.boilerplate.memberManager.mail.service.MailServiceImpl;
import com.hanghae99.boilerplate.memberManager.model.ResponseDto;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class MailController {

    @Autowired
    MailServiceImpl mailService;
    @Autowired
    ObjectMapper objectMapper;


    @PostMapping("/api/find/password")
    public ResponseDto findPassword(HttpServletResponse response, @Valid @RequestBody FindPasswordDto email, Errors errors) {

        if (errors.hasErrors())
            return new ResponseDto(HttpStatus.BAD_REQUEST,null,errors.getFieldError());
        try {
            mailService.sendFindPasswordVerifyMail(email.getEmail());
            return new ResponseDto(HttpStatus.OK,null,null);
        } catch (UsernameNotFoundException e) {
            response.setStatus(400);
            return new ResponseDto(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }

    }


    @GetMapping("/api/find/password/{key}")
    public ResponseDto changePassword(HttpServletResponse response, @PathVariable String key) throws JsonProcessingException {
        try {
            String password = mailService.isOkGiveNewPassword(key);
            return new ResponseDto(HttpStatus.OK, "OK", password);
        } catch (Exception E) {
            response.setStatus(401);
            return new ResponseDto(HttpStatus.UNAUTHORIZED, E.getMessage(), null);
        }
    }

}
