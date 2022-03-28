//package com.hanghae99.boilerplate.mvcTest.chat.controller;
//
//import com.hanghae99.boilerplate.chat.controller.ChatRoomController;
//import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;
//import com.hanghae99.boilerplate.chat.service.ChatRoomService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class ChatRoomControllerTest {
//
//    @Mock
//    private ChatRoomService chatRoomService;
//
//    ChatRoomController chatRoomController;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        chatRoomController = new ChatRoomController(chatRoomService);
//        mockMvc = MockMvcBuilders.standaloneSetup(chatRoomController).build();
//    }
//
//    @Test
//    @DisplayName("실시간채팅방 조회 OK")
//    public void onAirList() throws Exception {
//        //given
//        List<ChatRoomRedisDto> list = new ArrayList<>();
//        ChatRoomRedisDto redisDto1 = new ChatRoomRedisDto();
//        redisDto1.setRoomId(1L);
//        ChatRoomRedisDto redisDto2 = new ChatRoomRedisDto();
//        redisDto2.setRoomId(2L);
//        list.add(redisDto1);
//        list.add(redisDto2);
//
//        //when
//        when(chatRoomService.findOnAirChatRooms()).thenReturn(list);
//        String expectByRoomId = "$.[?(@.roomId == '%s')]";
//
//        //then
//        mockMvc.perform(get("/api/chat/rooms/onair"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath(expectByRoomId, "1").exists())
//                .andExpect(jsonPath(expectByRoomId, "2").exists())
//                .andExpect(jsonPath(expectByRoomId, "3").doesNotExist());
//    }
//
//
//}
