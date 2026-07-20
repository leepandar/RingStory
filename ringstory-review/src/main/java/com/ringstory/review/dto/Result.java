package com.ringstory.review.dto;
import lombok.AllArgsConstructor; import lombok.Data; import lombok.NoArgsConstructor;
@Data @NoArgsConstructor @AllArgsConstructor
public class Result<T> {
    private int code; private String message; private T data;
    public static <T> Result<T> success(T d) { return new Result<>(200,"success",d); }
}
