package com.hanghae99.boilerplate.memberManager.mail.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import com.hanghae99.boilerplate.memberManager.mail.FindPasswordDto;
import com.hanghae99.boilerplate.memberManager.mail.service.MailServiceImpl;
import com.hanghae99.boilerplate.memberManager.model.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;

@RestController
public class MailController {

    @Autowired
    MailServiceImpl mailService;


    @PostMapping("/api/find/password")
    public ResponseDto findPassword( @Valid @RequestBody FindPasswordDto email) throws MessagingException {

        mailService.sendFindPasswordVerifyMail(email.getEmail());
        return new ResponseDto(HttpStatus.OK,null,null);


    }


    @GetMapping("/api/find/password/{key}")
    public ResponseDto changePassword( @PathVariable String key) throws JsonProcessingException, AuthenticationException {
            String password = mailService.isOkGiveNewPassword(key);
            return new ResponseDto(HttpStatus.OK, "create new password", password);

    }

}
