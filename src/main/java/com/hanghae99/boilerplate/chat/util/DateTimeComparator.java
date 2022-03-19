package com.hanghae99.boilerplate.chat.util;

import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;

import java.time.LocalDateTime;
import java.util.Comparator;

public class DateTimeComparator implements Comparator<ChatRoomRedisDto> {
    @Override
    public int compare(ChatRoomRedisDto o1, ChatRoomRedisDto o2) {
        LocalDateTime time1 = o1.getCreatedAt();
        LocalDateTime time2 = o2.getCreatedAt();

        if (time1.isAfter(time2)) {
            return -1;
        } else if (time1.isBefore(time2)) {
            return 1;
        }
        return 0;
    }
}
