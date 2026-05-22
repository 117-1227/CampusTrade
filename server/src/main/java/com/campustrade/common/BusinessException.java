package com.campustrade.common;

/**
 * 业务异常，由 GlobalExceptionHandler 统一捕获。
 * 用法: throw new BusinessException("商品不存在")
 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
