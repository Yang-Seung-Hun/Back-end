package com.hanghae99.boilerplate.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 200, "요청에 성공하였습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }


}
