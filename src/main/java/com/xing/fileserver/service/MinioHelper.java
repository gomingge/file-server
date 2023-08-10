package com.xing.fileserver.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.collect.HashMultimap;
import com.xing.fileserver.common.constant.ResultCode;
import com.xing.fileserver.common.exception.BusinessException;
import com.xing.fileserver.config.MinioPropertiesConfig;
import com.xing.fileserver.pojo.rbo.*;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListPartsResponse;
import io.minio.ObjectWriteResponse;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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




}
