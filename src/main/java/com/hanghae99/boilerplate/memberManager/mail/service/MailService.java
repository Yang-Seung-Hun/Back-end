package com.hanghae99.boilerplate.memberManager.mail.service;

import javax.mail.MessagingException;

public interface MailService {
     void sendPasswordEmail(String email) throws MessagingException;

}
