package com.cherry.excption;

import com.cherry.domain.common.enums.ErrorCode;
import com.cherry.domain.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntime(RuntimeException e) {
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }
}
