package com.xing.fileserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UploadBindDTO implements Serializable {
    @NotBlank(message = "业务id不能为空")
    private String bizId;

    @NotEmpty(message = "文件不能为空")
    private List<String> fileIds;
}
