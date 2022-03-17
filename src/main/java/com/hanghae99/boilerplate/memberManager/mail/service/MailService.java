package com.hanghae99.boilerplate.memberManager.mail.service;

import java.util.Optional;

public interface MailService {

     Optional<String> verifyEmail(String email);

    Object verifyKey(String key);
}
