package com.xing.fileserver.service;

import cn.hutool.core.io.IoUtil;
import com.xing.fileserver.common.exception.BusinessException;
import com.xing.fileserver.config.MinioPropertiesConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
public class MinioService {

    @Autowired
    private MinioPropertiesConfig config;

    private MinioClient minioClient;

    @PostConstruct
    public void initClient() {
        try {
            this.minioClient = new MinioClient(config.getEndpoint(), config.getUser(), config.getPassword());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("Minio初始化异常");
        }
    }

    public String getBucket() {
        String bucket = Optional.ofNullable(config.getBucket()).orElse("default");
        try {
            boolean isExist = minioClient.bucketExists(bucket);
            if (!isExist) {
                minioClient.makeBucket(bucket);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("Minio创建桶异常");
        }
        return bucket;
    }

    /**
     * 直接上传
     *
     * @param file
     * @param path
     */
    public void save(MultipartFile file, String path) {
        String bucket = getBucket();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("Minio保存异常");
        }
    }

    /**
     * 直接下载
     *
     * @param path
     * @return
     */
    public byte[] get(String path) {
        byte[] data = new byte[0];
        try {
            InputStream is = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(getBucket())
                    .object(path)
                    .build());
            data = IoUtil.readBytes(is);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("Minio获取异常");
        }
        return data;
    }

    public void delete(String... paths) {

        if (paths.length == 0) {
            return;
        }
        String bucket = getBucket();
        for (String path : paths) {
            try {
                minioClient.removeObject(bucket, path);
            } catch (Exception e) {
                log.error("Minio删除{} {}异常", bucket, path, e.getMessage());
            }
        }
    }

    /**
     * 获取预签名上传地址
     *
     * @param path
     * @return
     */
    public String getPreSignedUploadUrl(String path) {
        String url = null;
        String bucket = getBucket();
        try {
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(getBucket())
                    .object(path)
                    .expiry(config.getExpire())
                    .extraQueryParams()
                    .build());
        } catch (Exception e) {
            log.error("Minio put  {} 预签异常", path);
            throw new BusinessException("Minio保存异常");
        }
        return url;
    }

    /**
     * 获取预签名下载url
     *
     * @param path 文件oss上保存路径
     * @return
     */
    public String getPreSignedDownloadUrl(String path) {
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(getBucket())
                            .object(path)
                            .expiry(config.getExpire())
                            .extraQueryParams()
                    .build());
        } catch (Exception e) {
            log.error("Minio put  {} 预签异常", path);
            throw new BusinessException("Minio保存异常");
        }

        return url;
    }

    /**
     * 获取对象信息和元数据
     *
     * @param path
     * @return
     */
    public StatObjectResponse stat(String path) {
        String bucket = getBucket();
        StatObjectResponse objectStat = null;

        try {
            objectStat = minioClient.statObject(bucket, path);
        } catch (Exception e) {
            log.error("Minio stat  {} 异常", path);
        }

        return objectStat;
    }

    public void createMultipartUpload(String path, Integer chunkSize) {
        minioClient.
    }

}
