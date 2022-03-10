package com.hanghae99.boilerplate.kakao.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.exception.TemporaryUser;
import com.hanghae99.boilerplate.kakao.KakaoUserInformationDto;
import com.hanghae99.boilerplate.kakao.common.Connection;
import com.hanghae99.boilerplate.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KakaoLoginService {


    @Autowired
    Connection connection;

    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RegisterMember registerMember;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RefreshTokenRepository  refreshTokenRepository;

    @JsonIgnoreProperties
    public void  getKakaoToken(HttpServletResponse response, String code) throws Exception {
        try {


            KakaoUserInformationDto user = connection.getaccessToken(code);

            TemporaryUser temporaryUser=  connection.getUserData(user.getAccess_token());

            Optional<LoginResponseDto> loginResponseDto= registerMember.register(temporaryUser);

            MemberContext memberContext = new MemberContext(loginResponseDto.get().getEmail(),
                   loginResponseDto.get().getRole().stream().map(role ->
                           new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));

            JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
            JwtToken refreshToken = tokenFactory.createRefreshToken(memberContext);

            refreshTokenRepository.save(new RefreshTokenDB(memberContext.getUsername(), refreshToken.getToken()));

            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.OK.value());
            Cookie cookie = new Cookie("Authentitcation",refreshToken.getToken());
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60* 48);
            cookie.setPath("/");
            response.addCookie(cookie);

            Map<String, String> tokenMap = new HashMap<String, String>();
            tokenMap.put("access_token", accessToken.getToken());
            tokenMap.put("email",temporaryUser.getEmail());
            tokenMap.put("nickname",temporaryUser.getNickname());

            objectMapper.writeValue(response.getWriter(),tokenMap);


        } catch (MalformedURLException e) {
            throw new MalformedURLException("bad request");
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


}