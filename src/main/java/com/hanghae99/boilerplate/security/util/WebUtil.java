package com.hanghae99.boilerplate.security.util;


import org.springframework.security.web.savedrequest.SavedRequest;

//컨텐츠 타입 검사
public class WebUtil {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static boolean isContentTypeJson(String cotentType){
        if(cotentType == null || cotentType.isBlank()||
        !CONTENT_TYPE_JSON.equals(cotentType)){
            return false;
        }
        return true;
    }
}
