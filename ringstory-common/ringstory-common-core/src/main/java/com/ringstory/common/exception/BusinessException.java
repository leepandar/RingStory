package com.ringstory.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final ErrorCode errorCode;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.errorCode = null;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.errorCode = null;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorCode = null;
    }

    /**
     * 使用 ErrorCode 枚举构造
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.errorCode = errorCode;
    }

    /**
     * 使用 ErrorCode 枚举 + 自定义消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.errorCode = errorCode;
    }
}
