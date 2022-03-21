package com.hanghae99.boilerplate.signupLogin.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.memberManager.mail.OnlyEmailDto;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.common.SetAuthorization;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.OnlyNicknameDto;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.signupLogin.dto.responseDto.ResponseDto;
import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    SetAuthorization setAuthorization;

    @PostMapping("/api/signup")
    public void signup(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody SignupReqestDto signupReqest) throws IOException {
        MemberContext memberContext= signupLoginService.signupRequest(signupReqest);
        //response.getWriter() 를 여기서 수행하게 되면         setAuthorization.runIfloginSuccess(response,memberContext)에서 스트림이 안먹히나봄 ??
//        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK, "signup success"));
        setAuthorization.runIfloginSuccess(response,memberContext);
    }



    @GetMapping("/api/user/check/email") //true : 중복
    public void checkDuplicatesEmail(HttpServletResponse response, @Valid @RequestBody OnlyEmailDto email) throws IOException {
        boolean result = signupLoginService.DuplicatesEmail(email.getEmail());
        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK, String.valueOf(result)));
    }

    @GetMapping("/api/user/check/nickname")
    public void checkDuplicatesNickname(HttpServletResponse response, @Valid @RequestBody OnlyNicknameDto nicknameDto) throws IOException {
        boolean result = signupLoginService.DuplicatePassword(nicknameDto.getNickname());
        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK, String.valueOf(result)));
    }

    @PostMapping("/api/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        signupLoginService.logoutRequest(request);
    }


}
