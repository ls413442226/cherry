package com.cherry.excption;

import com.cherry.domain.common.enums.ErrorCode;

/**
 * @author Aaliyah
 */
public class BusinessException extends RuntimeException{
    private ErrorCode message;

    public BusinessException(ErrorCode message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message.message();
    }
}
