package com.hanghae99.boilerplate.security.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationFailureHandler;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationSuccessHandler;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.security.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        if (!HttpMethod.POST.name().equals(request.getMethod()) ||
                !WebUtil.isContentTypeJson(request.getHeader(WebUtil.CONTENT_TYPE))) {
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.BAD_REQUEST,"알맞지 않은 httpMethod 이거나 ,전송된 데이터 타입이 [application/json]이 아닙니다"));
           return null;
        }

        try {
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getReader(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());
//            IOException, JsonParseException, JsonMappingException
            return this.getAuthenticationManager().authenticate(token);
        } catch (JsonParseException|JsonMappingException| NullPointerException e  ){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.BAD_REQUEST,e.getMessage()));

        }catch (BadCredentialsException e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.UNAUTHORIZED,e.getMessage()));
        }
        catch (Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            objectMapper.writeValue(response.getWriter(),ExceptionResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()));
        }
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
