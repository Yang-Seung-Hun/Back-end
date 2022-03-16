//package com.hanghae99.boilerplate;
//
//import com.carrotsearch.hppc.ObjectByteMap;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.security.Principal;
//
//@RestController
//public class TestController {
////
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @GetMapping("/auth/test")
//    public void test(HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {
//        if(principal==null)
//        {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setHeader("error","error");
//            return ;
//        }
//        MemberContext memberContext = (MemberContext)principal;
//        objectMapper.writeValue(response.getWriter(),memberContext);
//
//
//    }
//}
