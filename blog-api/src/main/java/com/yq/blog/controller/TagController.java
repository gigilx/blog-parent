package com.yq.blog.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.yq.blog.dao.mapper.TagMapper;
import com.yq.blog.service.TagService;
import com.yq.blog.vo.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tags")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 最热标签
     * @return
     */
    @GetMapping("hot")
    public  Result hotTags(){
        int limit = 6;
        return  tagService.hotTags(limit);
    }

    @GetMapping
    public Result tags(){
        return tagService.getAllTags();
    }

    @GetMapping("detail")
    public Result tagsDetails(){
        return tagService.getAllTagsDetails();
    }
    @GetMapping("detail/{id}")
    public Result tagDetails(@PathVariable("id")long id){
        return tagService.getTagDetailsByTagId(id);
    }
}
