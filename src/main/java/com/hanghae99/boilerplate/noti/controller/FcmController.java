package com.hanghae99.boilerplate.noti.controller;

import com.hanghae99.boilerplate.noti.dto.RequestFCMDTO;
import com.hanghae99.boilerplate.noti.service.FCMService;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class FcmController {
    private final FCMService firebaseCloudMessageService;

    @PostMapping("/api/fcm/register")
    public ResponseEntity registerMessage(@RequestBody RequestFCMDTO requestDTO, @AuthenticationPrincipal MemberContext memberContext) {
        System.out.println(requestDTO.getTargetToken());
        System.out.println(memberContext.getUsername());
        firebaseCloudMessageService.register(memberContext.getMemberId() , requestDTO.getTargetToken());

        return ResponseEntity.ok().build();
    }

}
