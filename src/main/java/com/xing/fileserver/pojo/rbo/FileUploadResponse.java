package com.xing.fileserver.pojo.rbo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author 沐明
 * @Date 2023/8/11 16:19
 * @Description
 */
@Data
@Builder
public class FileUploadResponse {
    private String realName;

    private String uploadName;

    private String url;

    private long size;

    private String bucket;
}
