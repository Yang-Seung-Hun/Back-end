package com.hanghae99.boilerplate.security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.Exception.AjaxAccessDeniedHandler;
import com.hanghae99.boilerplate.security.Exception.AjaxLoginAuthenticationEntryPoint;
import com.hanghae99.boilerplate.security.RefreshTokenEndPoint;
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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String SWAGGER = "/swagger-ui/**";
    public static final String SWAGGER_DOCS = "/swagger-resources/**";
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
    RefreshTokenEndPoint refreshTokenEndPoint;

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

    @Autowired
    ObjectMapper objectMapper;


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    //AUTHENTICATION_URL만 AjaxLoginProcessingFilter(로그인 담당(를지난다
    protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(AUTHENTICATION_URL,
                new AjaxAuthenticationSuccessHandler(tokenFactory, memberRepository, refreshTokenRepository, objectMapper), failureHandler);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //REFRESH_TOKEN_URL,와 AUTHENTICATION_URL는 스킵하고 API_ROOT_URL는 모두 인가처리해라
    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        List<String> pathsToSkip = Arrays.asList(REFRESH_TOKEN_URL, SIGNUP_URL, AUTHENTICATION_URL, SWAGGER, SWAGGER_DOCS);
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, AUTH_ROOT_URL);
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher
                , refreshTokenEndPoint,objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }


    @Override //csutom userDetailsService 와 passwordEncoder를  authenticationManger이 사용하도록
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.cors()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(deniedHandler)
                .authenticationEntryPoint(entryPoint);
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.logout()
                .logoutUrl("/api/logout/{email}")
                .deleteCookies(AUTHENTICATION_HEADER_NAME);
        http.
                authorizeRequests()
                .antMatchers(SIGNUP_URL, SWAGGER, SWAGGER_DOCS).permitAll()// Token refresh end-point
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);


    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedOrigin("http://localhost:3001");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));


        //주소를 특저애향함

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;

    }
}



