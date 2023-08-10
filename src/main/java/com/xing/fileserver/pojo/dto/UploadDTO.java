package com.xing.fileserver.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadDTO implements Serializable {

    /**
     * xxx爬虫下某篇文章下的雪花数id
     */
    private String bizId;

    /**
     * xx漫画，xx爬虫，xx网站
     */
    private String module = "default";
}
