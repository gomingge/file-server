package com.xing.fileserver.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UploadFinishedDTO implements Serializable {

    @NotEmpty(message = "文件id不能为空")
    private List<String> fileIds;
}
