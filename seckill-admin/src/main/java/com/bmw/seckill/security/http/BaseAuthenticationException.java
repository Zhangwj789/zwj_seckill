package com.bmw.seckill.security.http;


import com.bmw.seckill.common.base.BaseResponse;
import com.bmw.seckill.common.exception.ErrorMessage;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


@Getter
public class BaseAuthenticationException extends AuthenticationException {
    private Integer code;

    public BaseAuthenticationException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public static BaseAuthenticationException error(ErrorMessage message) {
        return new BaseAuthenticationException(message.getCode(), message.getMessage());
    }
    public BaseResponse resp() {
        return BaseResponse.error(code, getMessage());
    }
}
