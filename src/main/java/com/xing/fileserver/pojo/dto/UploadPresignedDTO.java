package com.xing.fileserver.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UploadPresignedDTO implements Serializable {

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotBlank(message = "文件MD5不能为空")
    private String fileMd5;

    @NotNull(message = "文件大小不能为空")
    private Double fileSize;

    @NotNull(message = "分片大小不能为空")
    private Double chunkSize;

    @NotBlank(message = "Content-Type不能为空")
    private String contentType;

    /**
     * 分类模块
     * 根据项目划分，比如动漫，游戏，漫画等
     */
    @NotBlank(message = "分类不能为空")
    private String module;
}
