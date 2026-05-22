package com.campustrade.common;

import lombok.Data;

/**
 * 统一 API 响应格式。
 * 静态工厂方法: Result.success(data) / Result.error(message)
 */
@Data
public class Result<T> {
    private int code;
    private T data;
    private String message;

    private Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "ok");
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, data, message);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(400, null, message);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message);
    }
}
