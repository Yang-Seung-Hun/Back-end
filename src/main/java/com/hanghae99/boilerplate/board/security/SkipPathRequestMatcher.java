package com.hanghae99.boilerplate.board.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class SkipPathRequestMatcher implements RequestMatcher {

    private OrRequestMatcher matchers;

    private RequestMatcher processingMathcer;

    public SkipPathRequestMatcher(List<String> pathToSkip, String processingPath) {
//        Assert.notNull(pathToSkip);
        List<RequestMatcher> m = pathToSkip.stream().map(path ->
                new AntPathRequestMatcher(path)).collect(Collectors.toList());
        matchers = new OrRequestMatcher(m);
        processingMathcer = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {

        //매치되는 경로들은 jwt인증을 수행하지 않는다
        if (matchers.matches(request)) {
            return false;
        }
        //매치가 안된다면 검증을 수행한다
        return processingMathcer.matches(request) ? true : false;

    }
}