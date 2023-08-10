package com.xing.fileserver.pojo.rbo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author 沐明
 * @Date 2023/8/10 18:13
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("分片上传创建响应类")
public class MultipartUploadCreateResponse {
    @ApiModelProperty("上传编号")
    private String uploadId;

    @ApiModelProperty("分片信息")
    private List<UploadCreateItem> chunks;

    private LocalDateTime expiryTime;


    @Data
    public static class UploadCreateItem {

        @ApiModelProperty("分片编号")
        private Integer partNumber;

        @ApiModelProperty("上传地址")
        private String uploadUrl;

    }
}
