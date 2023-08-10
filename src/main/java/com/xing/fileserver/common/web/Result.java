package com.xing.fileserver.common.web;

import com.xing.fileserver.common.constant.ResultCode;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Result<T> implements Serializable {

    private Integer code;

    private T data;

    private String message;


    public static Result success() {
        return Result.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .build();
    }

    public static Result success(Object data) {
        return Result.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static Result error() {
        return Result.builder()
                .code(ResultCode.KNOWN_ERROR.getCode())
                .message(ResultCode.KNOWN_ERROR.getMessage())
                .build();
    }

    public static Result error(Integer code) {
        final ResultCode resultCode = ResultCode.getByCode(code);
        return Result.builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .build();
    }

    public static Result error(Integer code, String message) {
        final ResultCode resultCode = ResultCode.getByCode(code);
        return Result.builder()
                .code(resultCode.getCode())
                .message(message)
                .build();
    }

}