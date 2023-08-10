package com.xing.fileserver.pojo.rbo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 沐明
 * @Date 2023/8/11 15:38
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("创建分片请求类")
public class MultipartUploadCreateRequest {

    /**
     * /模块/md5+sha256名字
     */
    private String objectName;

    @ApiModelProperty("分片数量")
    private Integer chunkSize;

    private String contentType;

}