package com.hanghae99.boilerplate.memberManager.mail.paltforms;


import org.springframework.stereotype.Component;

@Component
public class Google extends Platform{

    public Google(){
        host= "smtp.gmail.com";
        port=465;
        //나의 비밀정보~~!!
        sender ;
        password;
    }

}
