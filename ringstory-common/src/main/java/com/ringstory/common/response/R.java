package com.ringstory.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private String message;

    private T data;

    private long timestamp;

    public static <T> R<T> success() {
        return new R<>(200, "success", null, System.currentTimeMillis());
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data, System.currentTimeMillis());
    }

    public static <T> R<T> success(String message, T data) {
        return new R<>(200, message, data, System.currentTimeMillis());
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> error(String message) {
        return new R<>(500, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> unauthorized(String message) {
        return new R<>(401, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> forbidden(String message) {
        return new R<>(403, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> notFound(String message) {
        return new R<>(404, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> badRequest(String message) {
        return new R<>(400, message, null, System.currentTimeMillis());
    }
}
