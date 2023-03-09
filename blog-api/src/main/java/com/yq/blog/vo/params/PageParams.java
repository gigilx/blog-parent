package com.yq.blog.vo.params;

import lombok.Data;

@Data
public class PageParams {
    private Long categoryId;
    private String name;

    private int page = 1; //当前页数
    private int pageSize = 10; //页面大小

    private String sort;
    private Long tagId;
    private String year;
    private String month;

    public String getMonth(){
        if(this.month==null||this.month.length()==1)
            return "0"+this.month;
        return  this.month;
    }
}
