package com.cherry.excption;

import com.cherry.domain.common.enums.ErrorCode;
import com.cherry.domain.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

/**
 * 处理业务异常的全局异常处理器。
 * 当系统抛出BusinessException时，返回统一的错误响应。
 *
 * @param e 业务异常对象
 * @return Result<String> 包含系统错误码的失败结果
 */
@ExceptionHandler(BusinessException.class)
public Result<String> handleBusinessException(BusinessException e){
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

/**
 * 兜底异常处理器，处理所有未被捕获的通用异常。
 * 记录异常堆栈信息并返回系统错误响应。
 *
 * @param e 通用异常对象
 * @return Result<String> 包含系统错误码的失败结果
 */
@ExceptionHandler(Exception.class)
public Result<String> handleException(Exception e){
        e.printStackTrace();
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

/**
 * 处理运行时异常的全局异常处理器。
 * 当系统抛出RuntimeException时，返回统一的错误响应。
 *
 * @param e 运行时异常对象
 * @return Result<?> 包含系统错误码的失败结果
 */
@ExceptionHandler(RuntimeException.class)
public Result<?> handleRuntime(RuntimeException e) {

        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

}
