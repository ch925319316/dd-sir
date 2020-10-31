package com.web.mundo.service.impl;

import com.web.mundo.dao.IBookDao;
import com.web.mundo.dao.IChapterDao;
import com.web.mundo.po.Book;
import com.web.mundo.po.Chapter;
import com.web.mundo.service.IChapterService;
import com.web.mundo.vo.ChapterVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChapterServiceImpl implements IChapterService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IChapterDao chapterDao;
    @Autowired
    private IBookDao bookDao;

    @Override
    public Map<String,Object> queryDetailContent( String chapterId) {
        Map<String, Object> map = new HashMap<>();
        Chapter chapter = chapterDao.queryDetailContentByChapterId(chapterId);
        String filePath = chapter.getFilePath();
        if (chapter == null || StringUtils.isBlank(filePath)) {
            return null;
        }
        ChapterVO chapterVO = new ChapterVO();
        File file = new File(filePath);
        FileInputStream fileInputStream = null;
        try {
            Book book = bookDao.queryBookById(chapter.getBookId());
            fileInputStream = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            fileInputStream.read(b);
            String content = new String(b);
            chapterVO.setContent(content);
            chapterVO.setBookId(chapter.getBookId());
            chapterVO.setId(chapter.getId());
            chapterVO.setName(chapter.getName());
            map.put("book",book);
            map.put("chapter",chapterVO);
            return map;
        } catch (Exception e) {
            logger.error("文件读取失败",e);
        }finally {
            if (fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.error("流关闭失败",e);
                }
            }
        }
        return null;
    }

    @Override
    public Map<String,Object> queryAllChapters(String bookId) {
        Map<String, Object> map = new HashMap<>();
        Book book = bookDao.queryBookById(bookId);
        List<Chapter> chapters = chapterDao.queryAllDetails(bookId);
        if (chapters != null && !chapters.isEmpty()) {
            List<ChapterVO> chapterVOS = new ArrayList<>();
            for (Chapter chapter : chapters) {
                String filePath = chapter.getFilePath();
                ChapterVO chapterVO = new ChapterVO();
                File file = new File(filePath);
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    byte[] b = new byte[(int) file.length()];
                    fileInputStream.read(b);
                    String content = new String(b);
                    chapterVO.setContent(content);
                    chapterVO.setBookId(chapter.getBookId());
                    chapterVO.setId(chapter.getId());
                    chapterVO.setName(chapter.getName());
                    chapterVO.setBookName(book.getName());
                    chapterVOS.add(chapterVO);
                } catch (Exception e) {
                    logger.error("文件读取失败",e);
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            logger.error("流关闭失败",e);
                        }
                    }

            }}
            map.put("book",book);
            map.put("chapters",chapterVOS);
            return map;
        }

            return null;
    }

}
