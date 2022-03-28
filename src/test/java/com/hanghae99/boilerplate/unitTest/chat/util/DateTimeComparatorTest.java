//package com.hanghae99.boilerplate.unitTest.chat.util;
//
//import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;
//import com.hanghae99.boilerplate.chat.util.DateTimeComparator;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//class DateTimeComparatorTest {
//
//    @Test
//    @DisplayName("채팅방 리스트를 최근순 정렬")
//    void findOnAirChatRoomsOrdered() throws Exception {
//        //given
//        ChatRoomRedisDto oldRedisDto = new ChatRoomRedisDto();
//        oldRedisDto.setCreatedAt(LocalDateTime.now());
//        Thread.sleep(10);
//        ChatRoomRedisDto newRedisDto = new ChatRoomRedisDto();
//        newRedisDto.setCreatedAt(LocalDateTime.now());
//
//        List<ChatRoomRedisDto> redisDtoList = new ArrayList<>();
//        redisDtoList.add(oldRedisDto);
//        redisDtoList.add(newRedisDto);
//
//        //when
//        DateTimeComparator comparator = new DateTimeComparator();
//        Collections.sort(redisDtoList, comparator);
//
//        //then
//        LocalDateTime first = redisDtoList.get(0).getCreatedAt();
//        assertThat(first).isEqualTo(newRedisDto.getCreatedAt());
//
//    }
//
//}