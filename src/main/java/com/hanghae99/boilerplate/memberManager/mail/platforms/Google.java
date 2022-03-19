package com.hanghae99.boilerplate.memberManager.mail.platforms;


import org.springframework.beans.factory.annotation.Value;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Google {

    @Value("mail.host")
    String   host;
    @Value("mail.port")
    int  port;
    @Value("mail.sender")
    String  sender;
    @Value("mail.password")
    String  password;
    public void sendMail( String recipient,String title,String contents) throws MessagingException {
        Properties props = new Properties();
        // smtp 서버
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");


        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(sender, password);
            }
        });
        session.setDebug(true);

        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(sender));//발신자
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

        mimeMessage.setSubject(title);
        mimeMessage.setContent(contents, "text/html; charset=UTF-8");

        Transport.send(mimeMessage);


    }
}
