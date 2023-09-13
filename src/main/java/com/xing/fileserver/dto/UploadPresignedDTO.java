package com.xing.fileserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UploadPresignedDTO implements Serializable {

    // 文件名
    @NotBlank(message = "文件名不能为空")
    private String name;

    // 分类模块
    @NotBlank(message = "分类不能为空")
    private String module;
}
