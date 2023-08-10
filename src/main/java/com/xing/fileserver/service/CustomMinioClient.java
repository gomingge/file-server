package com.xing.fileserver.service;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * @Author 沐明
 * @Date 2023/8/9 15:40
 * @Description 主要增强分配上传功能
 */
public class CustomMinioClient extends MinioClient {
    public CustomMinioClient(MinioClient client) {
        super(client);
    }

    /**
     * 获取 uploadId
     *
     * @param bucketName       bucketName
     * @param region           region
     * @param objectName       objectName
     * @param headers          headers
     * @param extraQueryParams extraQueryParams
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws XmlParserException
     * @throws InvalidResponseException
     * @throws InternalException
     */
    public String getUploadId(String bucketName, String region, String objectName,
                              Multimap<String, String> headers, Multimap<String, String> extraQueryParams)
            throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        CreateMultipartUploadResponse response = this.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);

        return response.result().uploadId();
    }

    /**
     * 合并分片
     *
     * @param bucketName
     * @param region
     * @param objectName
     * @param uploadId
     * @param parts
     * @param extraHeaders
     * @param extraQueryParams
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws XmlParserException
     * @throws InvalidResponseException
     * @throws InternalException
     */
    @Override
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId,
                                              Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams)
            throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        return this.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

    /**
     * 查询分分片列表
     *
     * @param bucketName
     * @param region
     * @param objectName
     * @param maxParts
     * @param partNumberMaker
     * @param uploadId
     * @param extraHeaders
     * @param extraQueryParams
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws XmlParserException
     * @throws InvalidResponseException
     * @throws InternalException
     */
    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMaker,
                                           String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        return this.listParts(bucketName, region, objectName, maxParts, partNumberMaker, uploadId, extraHeaders, extraQueryParams);
    }
}
