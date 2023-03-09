package com.yq.blog.vo.params;

import com.yq.blog.vo.UserVo;
import lombok.Data;

@Data
public class CommentParams {
    private Long articleId;

    private String content;

    private Long toUserId;

    private Long parent;
}
