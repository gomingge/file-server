package com.xing.fileserver.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.collect.HashMultimap;
import com.xing.fileserver.config.MinioPropertiesConfig;
import com.xing.fileserver.pojo.rbo.MultipartUploadCreateResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 沐明
 * @Date 2023/8/10 17:52
 * @Description
 */
@Slf4j
@Component
public class MinioHelper {
    private final MinioPropertiesConfig minioPropertiesConfig;
    private final CustomMinioClient customMinioClient;

    @Autowired
    public MinioHelper(MinioPropertiesConfig minioPropertiesConfig, CustomMinioClient customMinioClient) {
        this.minioPropertiesConfig = minioPropertiesConfig;
        this.customMinioClient = customMinioClient;
    }



    /**
     * 初始化获取 uploadId
     * @param objectName 文件名
     * @param partCount 分片总数
     * @param contentType contentType
     * @return
     */
    public MultipartUploadCreateResponse createMultipartUpload(String objectName, int partCount, String contentType) {
        MultipartUploadCreateResponse response = new MultipartUploadCreateResponse();
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);

        String uploadId = "";
        List<String> partUrlList = new ArrayList<>();
        try {
            // 获取 uploadId
            uploadId = customMinioClient.getUploadId(minioPropertiesConfig.getBucketName(),
                    null,
                    objectName,
                    headers,
                    null);
            Map<String, String> paramsMap = new HashMap<>(2);
            paramsMap.put("uploadId", uploadId);
            for (int i = 1; i <= partCount; i++) {
                paramsMap.put("partNumber", String.valueOf(i));
                // 获取上传 url
                String uploadUrl = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        // 注意此处指定请求方法为 PUT，前端需对应，否则会报 `SignatureDoesNotMatch` 错误
                        .method(Method.PUT)
                        .bucket(minioPropertiesConfig.getBucketName())
                        .object(objectName)
                        // 指定上传连接有效期
                        .expiry(minioPropertiesConfig.getExpirySecond(), TimeUnit.SECONDS)
                        .extraQueryParams(paramsMap).build());
                MultipartUploadCreateResponse.UploadCreateItem item = new MultipartUploadCreateResponse.UploadCreateItem();

                partUrlList.add(uploadUrl);
            }
        } catch (Exception e) {
            log.error("initMultiPartUpload Error:" + e);
            return null;
        }
        // 过期时间
        LocalDateTime expireTime = LocalDateTimeUtil.offset(LocalDateTime.now(), minioPropertiesConfig.getExpirySecond(), ChronoUnit.SECONDS);
        MinioUploadInfo result = new MinioUploadInfo();
        result.setUploadId(uploadId);
        result.setExpiryTime(expireTime);
        result.setUploadUrls(partUrlList);
        return result;
    }
}
