package com.ringstory.user.dto;
import lombok.AllArgsConstructor; import lombok.Data; import lombok.NoArgsConstructor;
@Data @NoArgsConstructor @AllArgsConstructor
public class Result<T> {
    private int code; private String message; private T data;
    public static <T> Result<T> success() { return new Result<>(200,"success",null); }
    public static <T> Result<T> success(T data) { return new Result<>(200,"success",data); }
    public static <T> Result<T> error(int c, String m) { return new Result<>(c,m,null); }
}
