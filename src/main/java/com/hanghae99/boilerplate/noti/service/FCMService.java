package com.hanghae99.boilerplate.noti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final Map<Long, String> tokenMap = new HashMap<>();
    private final Map<String, String> messageMap = new HashMap<>();

    public void register(final Long userId, final String token) {
        tokenMap.put(userId, token);
    }

    public String getToken(Long id){
        return tokenMap.get(id);
    }

    private static final Logger logger = LoggerFactory.getLogger(FCMService.class);
    private static final String FIREBASE_CONFIG_PATH = "boiler-e3497-firebase-adminsdk-gedd3-21e30622fd.json";

    private final String API_URL= "https://fcm.google.api/v1/projects/boiler-e3497/message:send";
    private final ObjectMapper objectMapper;

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(FIREBASE_CONFIG_PATH))
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();;
        return googleCredentials.getAccessToken().getTokenValue();
    }

    public void sendMessageTo(Long userId, String title, String body) throws ExecutionException, InterruptedException, JsonProcessingException {
        //FCM에 대한 액세스를 승인하려면
        // This registration token comes from the client FCM SDKs.
        //String registrationToken = "YOUR_REGISTRATION_TOKEN";

        // See documentation on defining a message payload.
        // link?
        String targetToken = getToken(userId);
        messageMap.put("Df","Df");
        String _message = makeMessage(targetToken, title, body);
        Message message = Message.builder()
                .setToken(targetToken)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300").putAllData(
                        messageMap
                ).build())
                .setNotification(new Notification(title, body))
                .build();

        // Send a message to the device corresponding to the provided
        // registration token.
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        logger.info("Sent message: " + response);
        //    {
        //      "name":"projects/myproject-b5ae1/messages/0:1500415314455276%31bd1c9631bd1c96"
        //    }
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder().
                        token(targetToken).notification(
                                FcmMessage.Notification.builder()
                                        .title(title)
                                        .body(body)
                                        .image(null)
                                        .build()

                        ).build()
                ).validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

}
