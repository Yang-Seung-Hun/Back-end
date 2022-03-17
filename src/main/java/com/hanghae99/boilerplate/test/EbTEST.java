package com.hanghae99.boilerplate.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbTEST {

    @GetMapping("/api")
    public String    test(){
        return "hello world";
    }
}
