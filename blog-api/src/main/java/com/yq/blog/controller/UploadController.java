package com.yq.blog.controller;

import com.yq.blog.utils.QiniuUtils;
import com.yq.blog.vo.params.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){
        //图片重命名
        String fileName = UUID.randomUUID().toString() + "." +
                StringUtils.substringAfterLast(file.getOriginalFilename(),".");
        boolean upload = qiniuUtils.upload(file, fileName);
        if(upload){
            return Result.success(QiniuUtils.url + fileName);
        }
        return Result.fail(200001, "上传失败");

    }
}
