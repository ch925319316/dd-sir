package com.web.mundo.controller;

import com.web.mundo.config.ResultStatusEnum;
import com.web.mundo.service.IChapterService;
import com.web.mundo.vo.ResultDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("chapter")
public class ChapterController {


    @Autowired
    private IChapterService chapterService;


    @RequestMapping("content")
    @ResponseBody
    public ModelAndView bookDetail(String chapterId) {
        ResultDate resultDate = new ResultDate();
        Map<String, Object> content = chapterService.queryDetailContent( chapterId);
        if (content ==null ) {
            resultDate.setCode(ResultStatusEnum.NODATE.code);
        } else {
            resultDate.setData(content);
            resultDate.setCode(ResultStatusEnum.SUCCESS.code);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("content");
        modelAndView.getModel().put("data",resultDate);
        return modelAndView;
    }


    @RequestMapping("list")
    public ModelAndView bookList(String bookId) {
        ResultDate resultDate = new ResultDate();
        Map<String, Object> map = chapterService.queryAllChapters(bookId);
        if (map  == null) {
            resultDate.setCode(ResultStatusEnum.NODATE.code);
        } else {
            resultDate.setData(map);
            resultDate.setCode(ResultStatusEnum.SUCCESS.code);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("detail");
        modelAndView.getModel().put("data",resultDate);
        return modelAndView;
    }



}
