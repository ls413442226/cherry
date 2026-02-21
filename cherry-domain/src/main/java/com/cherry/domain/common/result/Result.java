package com.cherry.domain.common.result;

import com.cherry.domain.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.code(),
                ErrorCode.SUCCESS.message(),
                data);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.code(),
                errorCode.message(),
                null);
    }
}
