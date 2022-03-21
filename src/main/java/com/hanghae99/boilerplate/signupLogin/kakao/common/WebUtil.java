package com.hanghae99.boilerplate.signupLogin.kakao.common;

import com.hanghae99.boilerplate.security.model.MemberContext;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;


public class WebUtil {

    public static Cookie makeCookie(String name ,String value){
        Cookie cookie = new Cookie(name,value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60* 48);
        cookie.setPath("/");
        return cookie;
    }

    public static Map<String,String> UserDataToMap(MemberContext memberContext){
        Map<String, String> userData = new HashMap<String, String>();
        userData.put("nickname", memberContext.getNickname());
        userData.put("email", memberContext.getUsername());
        return userData;
    }
}
