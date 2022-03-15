package com.hanghae99.boilerplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/")
    public String t(){
        return "=====1=";
    }

    @GetMapping("/test")
    public String test(){
        return "hello";
    }
}
