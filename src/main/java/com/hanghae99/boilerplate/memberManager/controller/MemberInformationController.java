package com.hanghae99.boilerplate.memberManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.memberManager.service.MemberInformationService;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class MemberInformationController {

    @Autowired
    MemberInformationService memberInformationService;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/auth/users")
    public void getMemberInfromation(HttpServletResponse response, JwtAuthenticationToken jwtAuthenticationToken) throws IOException {
        MemberContext memberContext = (MemberContext) jwtAuthenticationToken.getPrincipal();
        Map<String, String> MemberData = memberInformationService.getMemberInformaiton(memberContext.getUsername());

        objectMapper.writeValue(response.getWriter(),MemberData);

    }



}