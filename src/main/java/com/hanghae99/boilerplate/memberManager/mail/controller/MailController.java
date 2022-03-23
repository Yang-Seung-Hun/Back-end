package com.hanghae99.boilerplate.memberManager.mail.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.memberManager.mail.OnlyEmailDto;
import com.hanghae99.boilerplate.memberManager.mail.service.MailServiceImpl;
import com.hanghae99.boilerplate.memberManager.model.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class MailController {

    @Autowired
    MailServiceImpl mailService;

    @Autowired
    ObjectMapper objectMapper;
    @GetMapping("/")
    public  void healthCheck(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(response.getWriter(),ResponseDto.of(HttpStatus.OK,"그만그만ㄱ므ㅏㄴ",null));
    }

    @PostMapping("/api/user/mypw")
    public ResponseDto findPassword(@Valid @RequestBody OnlyEmailDto email) throws MessagingException {

        mailService.sendPasswordEmail(email.getEmail());
        return new ResponseDto(HttpStatus.OK, null, null);


    }


//    @GetMapping("/api/find/password/{key}")
//    public ResponseDto changePassword( @PathVariable String key) throws JsonProcessingException, AuthenticationException {
//            String password = mailService.isOkGiveNewPassword(key);
//            return new ResponseDto(HttpStatus.OK, "create new password", password);
//
//    }

}
