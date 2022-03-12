package com.hanghae99.boilerplate.kakao.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.kakao.TemporaryUser;
import com.hanghae99.boilerplate.kakao.KakaoUserInformationDto;
import com.hanghae99.boilerplate.kakao.common.Connection;
import com.hanghae99.boilerplate.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Transactional
    public void  getKakaoUserInformaiton(HttpServletResponse response, String code) throws Exception {
        try {


            KakaoUserInformationDto user = connection.getaccessToken(code);

            TemporaryUser temporaryUser=  connection.getUserData(user.getAccess_token());

            log.info(temporaryUser.toString());

            Optional<LoginResponseDto> loginResponseDto= registerMember.registerKakaoUserToMember(temporaryUser);

            MemberContext memberContext = new MemberContext(loginResponseDto.get().getId() ,loginResponseDto.get().getEmail(),
                   loginResponseDto.get().getRole().stream().map(role ->
                           new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));

            JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
            JwtToken refreshToken = tokenFactory.createRefreshToken(memberContext);
            log.info(accessToken.toString());
            log.info(refreshToken.toString());

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
            log.info(e.toString()  );
            throw new MalformedURLException("bad request");
        } catch (IOException e) {
            log.info(e.toString()  );
            e.printStackTrace();
        }  catch (Exception e){
            log.info(e.toString()  );
            throw new Exception(e.getMessage());
        }
    }


}
