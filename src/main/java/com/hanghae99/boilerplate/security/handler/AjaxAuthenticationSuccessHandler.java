package com.hanghae99.boilerplate.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    ObjectMapper objectMapper;

    //인증 성공시 호출된다  //name이 null일 일이 없다
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberContext memberContext = new MemberContext(authentication.getName(), (List<GrantedAuthority>) authentication.getAuthorities());


        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }


    //저장되었을 수 있는 임시 인증 관련 데이터 제거
//     인증 프로세스 중 세션에서..
    protected  final void clearAuthenticationAttributes(HttpServletRequest request){
        HttpSession session = request.getSession();

        if(session==null){
            return ;
        }
        //필요 없는 데이터들을 security context
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
