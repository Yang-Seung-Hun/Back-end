package com.hanghae99.boilerplate.kakao.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.exception.TemporaryUser;
import com.hanghae99.boilerplate.kakao.KakaoUserInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Connection {

    @Autowired
    ObjectMapper   objectMapper;
    public KakaoUserInformationDto getaccessToken( String code) throws IOException {
        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        //문자열 처리 속도 향상
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("grant_type=authorization_code");
        stringBuilder.append("&client_id=91ee90dad2384a8f06ab7106b2f92daf");
        stringBuilder.append("&redirect_uri=http://localhost:8080/api/kakao/login");
        stringBuilder.append("&code=" + code);

        bufferedWriter.write(stringBuilder.toString());
        bufferedWriter.flush();

        int status = connection.getResponseCode();
        if(status != 200)
            return null;

        JsonNode jsonNode = readConnectionInput(connection);


        KakaoUserInformationDto user= objectMapper.treeToValue(jsonNode,KakaoUserInformationDto.class);
        bufferedWriter.close();


        return user;
    }

    public TemporaryUser getUserData(String token) throws IOException {

        String requestURL = "https://kapi.kakao.com/v2/user/me";

        URL url =new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + token);

        if(connection.getResponseCode() != 200)
            return null;

        JsonNode jsonNode = readConnectionInput(connection);
        String connectedAt= jsonNode.get("connected_at").textValue();
//        LocalDateTime register = StringToDate(connectedAt);
       String nickname= jsonNode.get("properties").get("nickname").textValue();
       String profileImageUrl = jsonNode.get("properties").get("profile_image").textValue();
       String email = jsonNode.get("kakao_account").get("email").textValue();

       TemporaryUser temporaryUser = new TemporaryUser(email ,nickname ,profileImageUrl);

       return temporaryUser;

    }



    public JsonNode readConnectionInput(HttpURLConnection connection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line="";
        String result ="";
        while((line= bufferedReader.readLine())!=null){
            result += line;
        }
        bufferedReader.close();
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(result);
        JsonNode jsonNode = objectMapper.readTree(result);
        return jsonNode;
    }

//    public LocalDateTime StringToDate(String date){
//
//        date=date.replace("T","&").replace("Z","&");
//        String pattern = "yyyy-MM-dd&HH:mm:ss&";
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//
//        LocalDateTime result = LocalDateTime.parse(date, formatter);
//
//        return result;
//    }
}