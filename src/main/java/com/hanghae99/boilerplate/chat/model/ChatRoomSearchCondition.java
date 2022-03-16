package com.hanghae99.boilerplate.chat.model;

import lombok.Data;

@Data
public class ChatRoomSearchCondition {
    // todo 검색조건에 더 들어갈 것이 있을까?
    private String keyword;
    private Boolean onAir;
}
