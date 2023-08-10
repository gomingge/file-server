package com.xing.fileserver.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import com.google.common.collect.HashMultimap;
import com.xing.fileserver.common.constant.ResultCode;
import com.xing.fileserver.common.exception.BusinessException;
import com.xing.fileserver.config.MinioPropertiesConfig;
import com.xing.fileserver.pojo.rbo.*;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioPropertiesConfig minioPropertiesConfig;

    private final CustomMinioClient customMinioClient;

    public String getBucket() {
        String bucket = Optional.ofNullable(minioPropertiesConfig.getBucketName()).orElse("default");
        try {
            boolean isExist = customMinioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build());
            if (!isExist) {
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
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
            customMinioClient.putObject(PutObjectArgs.builder()
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
            InputStream is = customMinioClient.getObject(GetObjectArgs.builder()
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
                customMinioClient.removeObject(RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(path)
                        .build());
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
            url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(getBucket())
                    .object(path)
                    .expiry(minioPropertiesConfig.getExpirySecond())
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
            url = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(getBucket())
                    .object(path)
                    .expiry(minioPropertiesConfig.getExpirySecond())
                    //.extraQueryParams()
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
            objectStat = customMinioClient.statObject(StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                    .build());
        } catch (Exception e) {
            log.error("Minio stat  {} 异常", path);
        }

        return objectStat;
    }


    /**
     * 创建分片上传地址
     *
     * @return
     */
    public MultipartUploadCreateResponse createMultipartUpload(MultipartUploadCreateRequest request) {
        MultipartUploadCreateResponse response = new MultipartUploadCreateResponse();
        List<MultipartUploadCreateResponse.UploadCreateItem> chunks = new ArrayList<>();
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", request.getContentType());

        String uploadId = "";
        try {
            // 获取 uploadId
            uploadId = customMinioClient.getUploadId(minioPropertiesConfig.getBucketName(),
                    null,
                    request.getObjectName(),
                    headers,
                    null);
            Map<String, String> paramsMap = new HashMap<>(2);
            paramsMap.put("uploadId", uploadId);
            for (int i = 1; i <= request.getChunkSize(); i++) {
                paramsMap.put("partNumber", String.valueOf(i));
                // 获取上传 url
                String uploadUrl = customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        // 注意此处指定请求方法为 PUT，前端需对应，否则会报 `SignatureDoesNotMatch` 错误
                        .method(Method.PUT)
                        .bucket(minioPropertiesConfig.getBucketName())
                        .object(request.getObjectName())
                        // 指定上传连接有效期
                        .expiry(minioPropertiesConfig.getExpirySecond(), TimeUnit.SECONDS)
                        .extraQueryParams(paramsMap).build());
                MultipartUploadCreateResponse.UploadCreateItem item = new MultipartUploadCreateResponse.UploadCreateItem();
                item.setUploadUrl(uploadUrl);
                item.setPartNumber(i);
                chunks.add(item);
            }
        } catch (Exception e) {
            log.error("initMultiPartUpload Error:" + e);
            return null;
        }
        // 过期时间
        LocalDateTime expireTime = LocalDateTimeUtil.offset(LocalDateTime.now(), minioPropertiesConfig.getExpirySecond(), ChronoUnit.SECONDS);
        response.setUploadId(uploadId);
        response.setChunks(chunks);
        response.setExpiryTime(expireTime);
        return response;
    }


    /**
     * 上传后合并分片合并
     *
     * @param uploadRequest
     */
    public FileUploadResponse completeMultipartUpload(CompleteMultipartUploadRequest uploadRequest) {
        log.info("文件合并开始, uploadRequest: [{}]", uploadRequest);
        try {
            final ListPartsResponse listMultipart = listMultipart(MultipartUploadCreate.builder()
                    .bucketName(minioPropertiesConfig.getBucketName())
                    .objectName(uploadRequest.getObjectName())
                    .maxParts(uploadRequest.getChunkSize() + 10)
                    .uploadId(uploadRequest.getUploadId())
                    .partNumberMarker(0)
                    .build());
            final ObjectWriteResponse objectWriteResponse = completeMultipartUpload(MultipartUploadCreate.builder()
                    .bucketName(minioPropertiesConfig.getBucketName())
                    .uploadId(uploadRequest.getUploadId())
                    .objectName(uploadRequest.getObjectName())
                    .parts(listMultipart.result().partList().toArray(new Part[]{}))
                    .build());

            return FileUploadResponse.builder()
                    //.url(minioPropertiesConfig.getDownloadUri() + "/" + minioPropertiesConfig.getBucketName() + "/" + uploadRequest.getObjectName())
                    .build();
        } catch (Exception e) {
            log.error("合并分片失败", e);
        }
        log.info("文件合并结束, uploadRequest: [{}]", uploadRequest);
        return null;
    }


    /**
     * 合并上传分片
     *
     * @param multipartUploadCreate
     * @return
     */
    public ObjectWriteResponse completeMultipartUpload(MultipartUploadCreate multipartUploadCreate) {
        try {
            return customMinioClient.completeMultipartUpload(multipartUploadCreate.getBucketName(), multipartUploadCreate.getRegion(),
                    multipartUploadCreate.getObjectName(), multipartUploadCreate.getUploadId(), multipartUploadCreate.getParts(),
                    multipartUploadCreate.getHeaders(), multipartUploadCreate.getExtraQueryParams());
        } catch (Exception e) {
            log.error("合并分片失败", e);
            throw BusinessException.newBusinessException(ResultCode.KNOWN_ERROR.getCode(), e.getMessage());
        }
    }

    public ListPartsResponse listMultipart(MultipartUploadCreate multipartUploadCreate) {
        try {
            return customMinioClient.listMultipart(multipartUploadCreate.getBucketName(), multipartUploadCreate.getRegion(), multipartUploadCreate.getObjectName(), multipartUploadCreate.getMaxParts(), multipartUploadCreate.getPartNumberMarker(), multipartUploadCreate.getUploadId(), multipartUploadCreate.getHeaders(), multipartUploadCreate.getExtraQueryParams());
        } catch (Exception e) {
            log.error("查询分片失败", e);
            throw BusinessException.newBusinessException(ResultCode.KNOWN_ERROR.getCode(), e.getMessage());
        }
    }


}
