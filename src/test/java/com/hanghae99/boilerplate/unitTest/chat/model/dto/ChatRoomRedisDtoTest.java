package com.hanghae99.boilerplate.unitTest.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.dto.ChatLeaveDto;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
class ChatRoomRedisDtoTest {

    @Test
    public void addAgree() throws Exception {
        //given
        ChatRoomRedisDto redisDto = new ChatRoomRedisDto();
        Long defaultCount = redisDto.getAgreeCount();

        //when
        Long afterAddedCount = redisDto.addAgree().getAgreeCount();

        //then
        assertThat(defaultCount).isEqualTo(0L);
        assertThat(afterAddedCount).isEqualTo(1L);
    }

    @Test
    public void subAgree() throws Exception {
        //given
        Long before = 7L;
        ChatRoomRedisDto redisDto = new ChatRoomRedisDto();
        redisDto.setAgreeCount(before);

        //when
        Long after = redisDto.subAgree().getAgreeCount();

        //then
        assertThat(after).isEqualTo(before - 1);
    }

    @Test
    public void recordRecentlyMemberVotedWhenLeaving() throws Exception {
        //given
        ChatRoomRedisDto redisDto = new ChatRoomRedisDto();
        redisDto.setRoomId(1L);

        Member member = new Member();
        Long memberId = member.getId();
        redisDto.getAgreed().put(memberId, false);
        redisDto.getDisagreed().put(memberId, false);

        //when
        ChatLeaveDto chatLeaveDto = new ChatLeaveDto();
        chatLeaveDto.setRoomId(1L);
        chatLeaveDto.setAgreed(true);
        chatLeaveDto.setDisagreed(false);

        redisDto.recordMemberAgreedOrDisagreed(member, chatLeaveDto);

        Boolean recentlyAgreed = redisDto.getAgreed().get(memberId);
        Boolean recentlyDisagreed = redisDto.getDisagreed().get(memberId);

        //then
        assertThat(recentlyAgreed).isTrue();
        assertThat(recentlyDisagreed).isFalse();
    }

}