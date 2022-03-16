package com.hanghae99.boilerplate.security.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationFailureHandler;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationSuccessHandler;
import com.hanghae99.boilerplate.security.model.login.      LoginRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    private ObjectMapper objectMapper=new ObjectMapper();



    //api/login에  대하여 이 필터가 작동함
    public AjaxLoginProcessingFilter(String defaultFilterProcessesUrl ,AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
                                     AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler) {
        super(defaultFilterProcessesUrl);
        this.ajaxAuthenticationFailureHandler= ajaxAuthenticationFailureHandler;
        this.ajaxAuthenticationSuccessHandler= ajaxAuthenticationSuccessHandler;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            log.info("HttpMethod not equal  POST");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.BAD_REQUEST,"Bad Reqest"));
           return null;
        }

        try {
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getReader(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());
//            IOException, JsonParseException, JsonMappingException
            return this.getAuthenticationManager().authenticate(token);
            //입력값에 대한 에러만 처리해준다
        } catch (JsonParseException|JsonMappingException| NullPointerException e  ){
            log.info(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.BAD_REQUEST,e.getMessage()));

        }
        //나머지 에러가 발생하면 unsuccessfulAuthentication로 이동한다
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws ServletException, IOException {
        //Called when a user has been successfully authenticated.
        ajaxAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, auth);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();;
        ajaxAuthenticationFailureHandler.onAuthenticationFailure(request,response,failed);
    }
}
