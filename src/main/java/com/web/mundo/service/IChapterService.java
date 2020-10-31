package com.web.mundo.service;


import com.web.mundo.vo.ChapterVO;

import java.util.List;
import java.util.Map;

public interface IChapterService {



    /**
     * 根据 图书id和章节id查找正文内容
     * @param chapterId
     * @return 所有章节列表 和 图书信息
     */
    Map<String,Object> queryDetailContent( String chapterId);

    /**
     * 根据图书id查找所有章节列表
     * @param bookId
     * @return 所有章节列表 和 图书信息
     */
    Map<String,Object> queryAllChapters(String bookId) ;


}
