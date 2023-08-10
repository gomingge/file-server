package com.xing.fileserver.pojo.rbo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author 沐明
 * @Date 2023/8/11 16:19
 * @Description
 */
@Data
@ApiModel("合并分片请求类")
public class CompleteMultipartUploadRequest {

    @ApiModelProperty("oss对象名称")
    private String objectName;

    @ApiModelProperty("上传编号")
    private String uploadId;

    @ApiModelProperty("分片数量")
    private Integer chunkSize;


    @ApiModelProperty("文件大小")
    private Integer fileSize;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("密码")
    private String pass;

    @ApiModelProperty("超时时间")
    private Integer expire;

    @ApiModelProperty("最大下载数")
    private Integer maxGetCount;
}
