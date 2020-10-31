package com.web.mundo.controller;

import com.alibaba.fastjson.JSON;
import com.web.mundo.config.ResultStatusEnum;
import com.web.mundo.dao.IBookDao;
import com.web.mundo.dao.IChapterDao;
import com.web.mundo.po.Book;
import com.web.mundo.po.Chapter;
import com.web.mundo.service.IBookService;
import com.web.mundo.service.IChapterService;
import com.web.mundo.util.MStringUtils;
import com.web.mundo.vo.BookVO;
import com.web.mundo.vo.ChapterVO;
import com.web.mundo.vo.ResultDate;
import com.web.mundo.worker.TestCrawler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("book")
public class BookController {

    @Autowired
    private TestCrawler crawler;

    @Autowired
    private IBookService bookServices;
    @Autowired
    private IChapterService chapterService;
    @Autowired
    private IChapterDao chapterDao;
    @Autowired
    private IBookDao bookDao;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping("start")
    public ModelAndView startCrawl(String bookName) {
        ResultDate resultDate = new ResultDate();
        String bookId = bookServices.queryBookIdByBookNameAndAuthor(bookName, "");
        if (StringUtils.isNotBlank(bookId)) {
            crawler.start(MStringUtils.trim(bookName));
            resultDate.setCode(ResultStatusEnum.SUCCESS.code);
            Map<String, Object> chapterVOS = chapterService.queryAllChapters(bookId);
            resultDate.setData(chapterVOS);
        }else {
            if (crawler.start(MStringUtils.trim(bookName))) {
                bookId = bookServices.queryBookIdByBookNameAndAuthor(bookName, "");
                resultDate.setCode(ResultStatusEnum.SUCCESS.code);
                Map<String, Object> chapterVOS = chapterService.queryAllChapters(bookId);
                resultDate.setData(chapterVOS);
            } else {
                resultDate.setCode(ResultStatusEnum.NODATE.code);
            }
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("detail");
        modelAndView.getModel().put("data",resultDate);
        return modelAndView;
    }


    @RequestMapping("recommend")
    @ResponseBody
    public String bookRecommend() {
        ResultDate resultDate = new ResultDate();
        List<BookVO> books = bookServices.queryRecommendBooks();
        if (books.isEmpty()) {
            resultDate.setCode(ResultStatusEnum.NODATE.code);
        } else {
            resultDate.setCode(ResultStatusEnum.SUCCESS.code);
        }
        resultDate.setData(books);
        return JSON.toJSONString(resultDate);
    }



    @RequestMapping("downTxt")
    public String downTxt(String bookId, HttpServletResponse response) {
        Book book = bookDao.queryBookById(bookId);
        if (book == null) {
            return null;
        }
        logger.info("开始下载,书名:{}",book.getName());
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("multipart/form-data");
        try {
            response.setHeader("Content-Disposition",
                    "attachment;fileName="+URLEncoder.encode(book.getName()+".txt", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OutputStream out = null;
        FileInputStream fileInputStream = null;
        try {
            out = response.getOutputStream();
            List<Chapter> chapters = chapterDao.queryAllDetails(bookId);
            for (Chapter chapter : chapters) {
                String filePath = chapter.getFilePath();
                File file = new File(filePath);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream =  new FileInputStream(file);
                fileInputStream.read(bytes);
                out.write(("\n"+chapter.getName()+"\n").getBytes());
                out.flush();
                out.write(bytes);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;
    }


}
