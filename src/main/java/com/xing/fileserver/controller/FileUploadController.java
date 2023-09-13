package com.xing.fileserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.xing.fileserver.common.model.PageResultBean;
import com.xing.fileserver.dto.*;
import com.xing.fileserver.service.FileUploadService;
import com.xing.fileserver.vo.UploadPresignedVO;
import com.xing.fileserver.vo.UploadVO;
import com.xing.fileserver.vo.UploadedFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Tag(name = "Minio", description = "Minio文件服务接口")
@RestController
@RequestMapping("files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Operation(summary = "上传文件")
    @PostMapping("upload")
    public UploadVO upload(@RequestParam(value = "file") MultipartFile file, UploadDTO uploadDTO) {
        UploadResultDTO uploadResultDTO = fileUploadService.upload(file, uploadDTO);
        UploadVO uploadVO = BeanUtil.toBean(uploadResultDTO, UploadVO.class);
        return uploadVO;
    }

    @Operation(summary = "预签-上传文件")
    @PostMapping("presigned")
    public UploadPresignedVO uploadPresigned(@Validated @RequestBody UploadPresignedDTO dto) {
        UploadPresignedResultDTO uploadPresignedResultDTO = fileUploadService.uploadPresigned(dto);
        UploadPresignedVO uploadPresignedVO = BeanUtil.toBean(uploadPresignedResultDTO, UploadPresignedVO.class);
        return uploadPresignedVO;
    }

    @Operation(summary = "预签-上传文件结束")
    @PostMapping("finished")
    public int uploadFinished(@Validated @RequestBody UploadFinishedDTO dto) {
        int count = fileUploadService.uploadFinished(dto);
        return count;
    }

    @Operation(summary = "文件列表")
    @GetMapping("")
    public PageResultBean<UploadedFileVO> getFiles(UploadFilePageDTO dto) {
        PageResultBean<UploadedFileDTO> pageResultBean = fileUploadService.listFiles(dto);
        List<UploadedFileDTO> files = pageResultBean.getItems();
        List<UploadedFileVO> data = BeanUtil.copyToList(files, UploadedFileVO.class);
        return new PageResultBean(pageResultBean.getTotal(), data);
    }

    @Operation(summary = "下载文件")
    @GetMapping("download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) {
        fileUploadService.download(id, response);
    }

    @Operation(summary = "业务id与文件绑定")
    @PostMapping("bind")
    public int bind(@Validated @RequestBody UploadBindDTO bindDTO) {
        return fileUploadService.bind(bindDTO);
    }


    @Operation(summary = "文件信息")
    @GetMapping("{id}")
    public UploadedFileVO getById(@PathVariable String id) {
        UploadedFileDTO uploadedFileDTO = fileUploadService.getById(id);
        UploadedFileVO uploadedFileVO = null;
        if (Objects.nonNull(uploadedFileDTO)) {
            uploadedFileVO = BeanUtil.toBean(uploadedFileDTO, UploadedFileVO.class);
        }
        return uploadedFileVO;
    }

    @Operation(summary = "通过文件id删除文件")
    @DeleteMapping("{id}")
    public int deleteById(@PathVariable String id) {
        int count = fileUploadService.delete(id);
        return count;
    }

    @Operation(summary = "通过业务id删除文件")
    @DeleteMapping("biz/{bizId}")
    public int deleteByBizId(@PathVariable String bizId) {
        int count = fileUploadService.deleteByBizId(bizId);
        return count;
    }


    @Operation(summary = "通过业务id获取文件")
    @GetMapping("biz/{bizId}")
    public List<UploadedFileVO> getBizFiles(@PathVariable String bizId) {
        List<UploadedFileDTO> uploadedFileDTOS = fileUploadService.getByBizId(bizId);
        List<UploadedFileVO> uploadedFileVOS = BeanUtil.copyToList(uploadedFileDTOS, UploadedFileVO.class);
        return uploadedFileVOS;
    }


    @Operation(summary = "获取预签url")
    @GetMapping("presigned/{id}")
    public UploadPresignedVO getPresignedUrl(@PathVariable String id) {
        UploadPresignedResultDTO uploadPresignedResultDTO = fileUploadService.getPresigned(id);
        UploadPresignedVO uploadPresignedVO = BeanUtil.toBean(uploadPresignedResultDTO, UploadPresignedVO.class);
        return uploadPresignedVO;
    }


//    @Operation(summary = "测试 @FillFiles")
//    @GetMapping("test-fill-files/{bizId}")
//    public FillFilesTestVO testFillFills(@PathVariable String bizId) {
//        FillFilesTestVO vo = new FillFilesTestVO();
//        vo.setBizId(bizId);
//        return vo;
//    }


}
