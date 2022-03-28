//package com.hanghae99.boilerplate.unitTest.chat.service;
//
//import com.hanghae99.boilerplate.chat.model.ChatRole;
//import com.hanghae99.boilerplate.chat.model.dto.ChatCloseDto;
//import com.hanghae99.boilerplate.chat.model.dto.CreateChatRoomDto;
//import com.hanghae99.boilerplate.chat.repository.ChatEntryRepository;
//import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
//import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
//import com.hanghae99.boilerplate.chat.service.ChatRoomServiceImpl;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class ChatRoomServiceImplTest {
//
//    @Mock
//    private ChatRoomRepository chatRoomRepository;
//    @Mock
//    private RedisChatRoomRepository redisChatRoomRepository;
//    @Mock
//    private MemberRepository memberRepository;
//    @Mock
//    private ChatEntryRepository chatEntryRepository;
//
//    @InjectMocks
//    private ChatRoomServiceImpl chatRoomServiceImpl;
//
//    @Test
//    @Transactional
//    @DisplayName("채팅방 생성 - 회원 id 없을 경우")
//    void createChatRoomTest() throws Exception {
//        //given
//        Long invalidId = -1L;
//        MemberContext member = new MemberContext();
//        member.setMemberId(invalidId);
//        CreateChatRoomDto createChatRoomDto = new CreateChatRoomDto();
//
//        given(memberRepository.findById(invalidId)).willReturn(null);
//
//        //when
//        String expectedExceptionMessage = "";
//        try {
//            chatRoomServiceImpl.createChatRoom(createChatRoomDto, member);
//        } catch (IllegalArgumentException e) {
//            expectedExceptionMessage = e.getMessage();
//        }
//
//        //then
//        assertThat(expectedExceptionMessage).isEqualTo("해당 ID의 회원이 존재하지 않습니다.");
//    }
//
//    @Test
//    @Transactional
//    @DisplayName("채팅방 종료 - MODERATOR가 아닐 경우")
//    void closeRoomByUnqualified() throws Exception {
//        //given
//        ChatCloseDto chatCloseDto = new ChatCloseDto();
//        chatCloseDto.setRole(ChatRole.SUBSCRIBER);
//        MemberContext member = new MemberContext();
//
//        //when
//        String expectedExceptionMessage = "";
//        try {
//            chatRoomServiceImpl.closeRoom(chatCloseDto, member);
//        } catch (IllegalArgumentException e) {
//            expectedExceptionMessage = e.getMessage();
//        }
//
//        //then
//        assertThat(expectedExceptionMessage).isEqualTo("방장만이 방을 삭제할 수 있습니다.");
//    }
//
//
//
//}