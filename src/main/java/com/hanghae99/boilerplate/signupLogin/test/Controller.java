package com.hanghae99.boilerplate.signupLogin.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.signupLogin.kakao.common.SetAuthorization;
import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class Controller {
    @Autowired
    Service service;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SetAuthorization setAuthorization;

    @PostMapping("/api/test/signup")
    public void signup(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody SignupReqestDto signupReqest) throws IOException {
        MemberContext memberContext= service.signupRequest(signupReqest);
        //response.getWriter() 를 여기서 수행하게 되면         setAuthorization.runIfloginSuccess(response,memberContext)에서 스트림이 안먹히나봄 ??
//        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK, "signup success"));
        setAuthorization.runIfloginSuccess(response,memberContext);
    }

}
