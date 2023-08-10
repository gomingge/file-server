package com.xing.fileserver.common.exception;

import com.xing.fileserver.common.constant.ResultCode;
import lombok.Builder;
import lombok.Getter;

/**
 * 业余异常
 */
@Getter
@Builder
public class BusinessException extends RuntimeException {
    // 错误码
    private Integer code;

    //错误消息
    private String msg;


    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String message) {
        super(message);
        this.msg = message;
    }

    public static BusinessException newBusinessException(Integer code, String message) {
        return BusinessException.builder().msg(message).code(code).build();
    }

    public static BusinessException newBusinessException(Integer code) {
        return BusinessException.builder()
                .code(code)
                .msg(ResultCode.getByCode(code).getMessage())
                .build();
    }
}
