package com.hanghae99.boilerplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/test")
    public String test(){
        return "hello";
    }
    @GetMapping("/")
    public String test2(){
        return "root";
    }
}
