package com.xing.fileserver.pojo.rbo;

import com.google.common.collect.Multimap;
import io.minio.messages.Part;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 沐明
 * @Date 2023/8/11 15:46
 * @Description 创建分片上传需要的参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultipartUploadCreate {

    private String bucketName;
    private String region;
    private String objectName;
    private Multimap<String, String> headers;
    private Multimap<String, String> extraQueryParams;

    private String uploadId;

    private Integer maxParts;

    private Part[] parts;

    private Integer partNumberMarker;


}
