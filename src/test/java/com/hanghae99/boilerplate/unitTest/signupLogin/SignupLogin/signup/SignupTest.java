//package com.hanghae99.boilerplate.unitTest.signupLogin.SignupLogin.signup;
//
//import com.hanghae99.boilerplate.memberManager.model.Member;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class SignupTest {
//
//
//    @Mock
//    MemberRepository memberRepository;
//    @Spy
//    PasswordEncoder passwordEncoder;
//
//
//    @InjectMocks
//    SignupLoginService signupLoginService=new SignupLoginService();
//    SignupReqestDto noramlSignupRequest = new SignupReqestDto("wns674@naver.com","1234","nickname","password");
//
//
//    @Test
//    @DisplayName("정상적인 회원가입 요청")
//    void requestSignup(){
//        Mockito.when(memberRepository.save(any(Member.class))).thenReturn(new Member(noramlSignupRequest));
//        signupLoginService.signupRequest(noramlSignupRequest);
//    }
//
//
//
//}
