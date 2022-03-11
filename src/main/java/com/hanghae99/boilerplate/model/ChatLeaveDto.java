package com.hanghae99.boilerplate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatLeaveDto {
    Long roomId;
    String memberName;
    String role;
    String token;
}

