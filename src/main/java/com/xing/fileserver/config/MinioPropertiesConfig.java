package com.xing.fileserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "minio")
@Configuration
@Data
public class MinioPropertiesConfig {

    /**
     * 服务器 URL
     */
    private String endpoint;

    /**
     * Access key
     */
    private String accessKey;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * 默认的 bucket 名称
     */
    private String bucketName;

    /**
     * 运行上传的文件类型
     */
    private String allowFileType;

    /**
     * 预签名上传,预签名下载，分片上传有效期（单位：秒）
     */
    private Integer expirySecond;
}
