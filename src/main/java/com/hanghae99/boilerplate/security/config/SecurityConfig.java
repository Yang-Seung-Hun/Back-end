package com.hanghae99.boilerplate.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.Exception.AjaxAccessDeniedHandler;
import com.hanghae99.boilerplate.security.Exception.AjaxLoginAuthenticationEntryPoint;
import com.hanghae99.boilerplate.security.filter.AjaxLoginProcessingFilter;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationFailureHandler;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationSuccessHandler;
import com.hanghae99.boilerplate.security.provider.AjaxAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String AUTHENTICATION_URL = "/api/login";

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AjaxAuthenticationProvider ajaxAuthenticationProvider;

    @Autowired
    AjaxLoginAuthenticationEntryPoint entryPoint;
    @Autowired
    AjaxAccessDeniedHandler deniedHandler;




    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEntryPoint) throws Exception {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint,
                new AjaxAuthenticationSuccessHandler(),new AjaxAuthenticationFailureHandler());
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Override //csutom userDetailsService 와 passwordEncoder를  authenticationManger이 사용하도록
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider);
        String password = passwordEncoder().encode("1111");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.inMemoryAuthentication().withUser("user").password(password).roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password(password).roles("ADMIN");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(
                AUTHENTICATION_URL
        );
        http.csrf().disable()
                        .exceptionHandling()
                                .accessDeniedHandler(deniedHandler)
                                        .authenticationEntryPoint(entryPoint);

        http.
                authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(buildAjaxLoginProcessingFilter(AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class);
    }
}
