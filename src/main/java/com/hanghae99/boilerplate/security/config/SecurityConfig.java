package com.hanghae99.boilerplate.security.config;


import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.Exception.AjaxAccessDeniedHandler;
import com.hanghae99.boilerplate.security.Exception.AjaxLoginAuthenticationEntryPoint;
import com.hanghae99.boilerplate.security.SkipPathRequestMatcher;
import com.hanghae99.boilerplate.security.filter.AjaxLoginProcessingFilter;
import com.hanghae99.boilerplate.security.filter.JwtTokenAuthenticationProcessingFilter;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationFailureHandler;
import com.hanghae99.boilerplate.security.handler.AjaxAuthenticationSuccessHandler;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenExtractor;
import com.hanghae99.boilerplate.security.provider.AjaxAuthenticationProvider;
import com.hanghae99.boilerplate.security.provider.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String AUTHENTICATION_HEADER_NAME = "Authentitcation";
    public static final String AUTHENTICATION_URL = "/api/login";
    public static final String AUTH_ROOT_URL = "/auth/**";
    public static final String REFRESH_TOKEN_URL = "/api/token";
    public static final String SIGNUP_URL = "/api/**";


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

    @Autowired
    JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    AjaxAuthenticationFailureHandler failureHandler;
    @Autowired
    AjaxAuthenticationSuccessHandler successHandler;

    @Autowired
    TokenExtractor tokenExtractor;
    @Autowired
    TokenFactory tokenFactory;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
//    @Bean
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper();
//    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    //AUTHENTICATION_URL만 AjaxLoginProcessingFilter를지난다
    protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(AUTHENTICATION_URL,
                new AjaxAuthenticationSuccessHandler(tokenFactory, memberRepository,refreshTokenRepository), failureHandler);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //REFRESH_TOKEN_URL,와 AUTHENTICATION_URL는 스킵하고 API_ROOT_URL는 모두 인가처리해라
    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        List<String> pathsToSkip = Arrays.asList(REFRESH_TOKEN_URL ,SIGNUP_URL, AUTHENTICATION_URL);
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, AUTH_ROOT_URL);
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }


    @Override //csutom userDetailsService 와 passwordEncoder를  authenticationManger이 사용하도록
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
        String password = passwordEncoder.encode("1111");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.inMemoryAuthentication().withUser("user").password(password).roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password(password).roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .exceptionHandling()
                .accessDeniedHandler(deniedHandler)
                .authenticationEntryPoint(entryPoint);
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.
                authorizeRequests()
                .antMatchers(REFRESH_TOKEN_URL,SIGNUP_URL).permitAll()// Token refresh end-point
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);


    }
}
