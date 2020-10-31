package com.web.mundo.worker;

import com.web.mundo.dao.IBookDao;
import com.web.mundo.dao.IChapterDao;
import com.web.mundo.po.Book;
import com.web.mundo.po.Chapter;
import com.web.mundo.service.IBookService;
import com.web.mundo.service.ProxyService;
import com.web.mundo.util.FileUtils;
import com.web.mundo.util.HttpClientDownloader;
import com.web.mundo.util.IdUtils;
import com.web.mundo.util.MStringUtils;
import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.xsoup.Xsoup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TestCrawler {

    private static final Map<String, ThreadPoolExecutor> executorMap = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IChapterDao chapterDao;
    @Autowired
    private IBookDao bookDao;
    @Autowired
    private IBookService bookService;

    private String qbiqucomindexUrl = "http://www.qbiqu.com/modules/article/search.php?searchkey=";
    private String guibuyuorgindexUrl = "https://www.guibuyu.org/modules/article/search.php?searchkey={0}&searchtype=articlename";

    private String host1 = "www.qbiqu.com";



    /**
     * 获得一个代理对象
     * @param host
     * @return
     */
    protected ProxyVO getProxy(String host){
        ProxyVO proxy = null;
        while(proxy == null){
            proxy = ProxyService.getProxy(host, 1, 4);
            if (proxy == null) {
                try {
                    logger.info("等待有效代理返回。。。");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return proxy;
    }


    public boolean start(String bookName) {
        if (StringUtils.isBlank(bookName)) {
            return false;
        }
        String bookId = bookService.queryBookIdByBookNameAndAuthor(bookName, "");
        // 数据库中已存在该书
        if (StringUtils.isNotBlank(bookId)) {
            Thread thread1 = new Thread(() -> {
                searchBook(bookName);
            });
            thread1.start();
            return true;
        }
        // 开始查找
        return searchBook(bookName);

    }

    /**
     * 开始搜索书名
     * @param bookName
     * @return
     */
    private boolean searchBook(String bookName) {
        if (downqbiqucomindex(bookName)) {
            return true;
        }
        if (downguibuyuorgindex(bookName)) {
            return true;
        }
        return false;
    }

    private boolean downqbiqucomindex(String bookName) {
        try {
            String encode = URLEncoder.encode(bookName, "gbk");
            // 获得 所有详情页的链接
            List<Chapter> detailUrls = getqbiqucomindexList(qbiqucomindexUrl + encode, bookName);
            if (detailUrls != null && (!detailUrls.isEmpty())) {
                ThreadPoolExecutor executor = executorMap.get(bookName);
                if (executor == null) {
                    executor = new ThreadPoolExecutor(4, 4, 6, TimeUnit.HOURS, new LinkedBlockingQueue<>());
                    executorMap.put(bookName, executor);
                }
                for (Chapter detailUrl : detailUrls) {
                    executor.execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    handleqbiqucomindexDetailPage(detailUrl);
                                }
                            }
                    );
                }
                logger.info("host:{},共有线程数:{}",bookName,executor.getTaskCount());
                logger.info("host:{},当前排队线程数:{}",bookName,executor.getQueue().size());
                logger.info("host:{},当前活动线程数:{}",bookName,executor.getActiveCount());
                logger.info("host:{},执行完成线程数:{}",bookName,executor.getCompletedTaskCount());
                checkPool(executor,bookName);
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return false;
    }

    private boolean downguibuyuorgindex(String bookName) {
        try {
            String encode = URLEncoder.encode(bookName, "gbk");
            String url = MessageFormat.format(guibuyuorgindexUrl,encode);
            // 获得 所有详情页的链接
            List<Chapter> detailUrls = getguibuyuorgindexList(url, bookName);
            if (detailUrls != null && (!detailUrls.isEmpty())) {
                ThreadPoolExecutor executor = executorMap.get(bookName);
                if (executor == null) {
                    executor = new ThreadPoolExecutor(4, 4, 6, TimeUnit.HOURS, new LinkedBlockingDeque<>());
                    executorMap.put(bookName, executor);
                }
                for (Chapter detailUrl : detailUrls) {
                    executor.execute(
                            new Runnable() {
                                @Override
                                public void run() {
                                    handleguibuyuorgindexDetailPage(detailUrl);
                                }
                            }
                    );
                }
                logger.info("host:{},共有线程数:{}",bookName,executor.getTaskCount());
                logger.info("host:{},当前排队线程数:{}",bookName,executor.getQueue().size());
                logger.info("host:{},当前活动线程数:{}",bookName,executor.getActiveCount());
                logger.info("host:{},执行完成线程数:{}",bookName,executor.getCompletedTaskCount());
                checkPool(executor,bookName);
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return false;
    }



    /**
     * 检查线程池执行了多少
     * @param executor
     * @param host
     */
    private void checkPool(ThreadPoolExecutor executor,String host){
        Thread thread1 = new Thread(() -> {
            while(true){
                long taskCount = executor.getTaskCount();
                long completedTaskCount = executor.getCompletedTaskCount();
                logger.info("总线程数:{},已完成:{},host:{}",taskCount,completedTaskCount,host);
                if (taskCount == completedTaskCount) {
                    logger.info("线程已执行完毕");
                    return;
                } else {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        logger.error("线程睡眠错误",e);
                    }
                }


            }
        });
        thread1.start();
        logger.info("核查线程已启动");
    }

    /**
     * 下载并解析详情页
     * @param chapter
     */
    private void handleqbiqucomindexDetailPage(Chapter chapter) {
        Chapter oneByBookIdAndTitle = chapterDao.findOneByBookIdAndTitle(chapter.getBookId(), chapter.getName());
        if (oneByBookIdAndTitle != null) {
            return;
        }
        try {
            Request request = new Request(chapter.getUrl());
            request.setCharSet("GBK");
            request.putDateMap("proxy", getProxy(host1));
            int retrye = 0;
            while (retrye < 6) {
                retrye++;
                Page page = HttpClientDownloader.download(request);
                Thread.sleep(8000);
                if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                    String detailContent = Xsoup.select(page.getRawText(), "//div[@id='content']").get();
                    String uuid = IdUtils.getUUID();
                    String filePath = MessageFormat.format(FileUtils.DIR_PATH,chapter.getBookId())+"/"+uuid ;
                    if (StringUtils.isNotBlank(detailContent)) {
                        FileUtils.saveContentToFile(detailContent,filePath);
                    }
                    chapter.setFilePath(filePath);
                    chapter.setCreateTime(new Date());
                    chapter.setUpdateTime(new Date());
                    chapter.setId(uuid);
                    chapterDao.saveChapter(chapter);
                    logger.info("正文储存成功,title:{}",chapter.getName());
                    return;
                }
            }
            logger.info("页面下载失败,title:{},url:{}",chapter.getName(),chapter.getUrl());
        } catch (Exception e) {
            logger.error("正文解析储存出错",e);
        }
    }

    /**
     * 获取详情页的链接
     *
     * @param searcUrl
     * @return
     */
    private List<Chapter> getqbiqucomindexList(String searcUrl, String bookName) {
        // 得到真实列表页的链接
        String listUrl = getqbiqucomindexListUrl(searcUrl, bookName);
        if (StringUtils.isBlank(listUrl)) {
            return null;
        }
        Request request = new Request(listUrl);
        request.setCharSet("GBK");
        request.putDateMap("proxy", getProxy(host1));

        int retrye = 0;
        while (retrye < 6) {
            retrye++;
            // 下载列表页  获得所有详情页链接
            Page page = HttpClientDownloader.download(request);
            if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                String listDiv = Xsoup.select(page.getRawText(), "//div[@id='list']/dl").get();
                // 得到作者
                String author = Xsoup.select(page.getRawText(), "//div[@id='info']/p[1]/text()").get();
                Book book1 = bookDao.fondOneByNameAndAuthor(bookName, author);
                String bookId ;
                if (book1 != null) {
                    bookId = book1.getId();
                    bookDao.updateBoodById(new Date(),bookId);
                } else {
                    // 得到简介
                    String intro = Xsoup.select(page.getRawText(), "//div[@id='intro']/p[1]/text()").get();
                    bookId =  IdUtils.getUUID();
                    Book book = new Book();
                    book.setId(bookId);
                    book.setUrl(listUrl);
                    book.setAuthor(MStringUtils.trim(author));
                    book.setIntroduction(MStringUtils.trim(intro));
                    book.setCreateTime(new Date());
                    book.setUpdateTime(new Date());
                    book.setName(bookName);
                    String dirPath = MessageFormat.format(FileUtils.DIR_PATH, bookId);
                    FileUtils.chechFileDir(dirPath);
                    bookDao.saveBook(book);
                }
                Pattern pattern = Pattern.compile("正文\\s*</dt>([\\s\\S]*)</dl>");
                Matcher matcher = pattern.matcher(listDiv);
                if (matcher.find()) {
                    String listDds = matcher.group(1);
                    List<String> listUrls = Xsoup.select(listDds, "//a/@href").list();
                    List<String> titles = Xsoup.select(listDds, "//a/text()").list();
                    List<Chapter> chapters = new ArrayList<>();
                    String substring = listUrl.substring(0, listUrl.lastIndexOf("/"));
                    if (listUrls != null && (!listUrls.isEmpty())) {
                        int count = 1;
                        for (int i = 0; i < listUrls.size(); i++) {
                            Chapter chapter = new Chapter();
                            chapter.setUrl(substring + listUrls.get(i));
                            chapter.setCount(count);
                            chapter.setName(titles.get(i));
                            chapter.setBookId(bookId);
                            chapters.add(chapter);
                            count++;
                        }
                    }
                    listUrls = null;
                    return chapters;
                }
                break;
            }
        }
        return null;
    }


    /**
     * 获取真实详情页的链接
     *
     * @param listUrl
     * @return
     */
    private String getqbiqucomindexListUrl(String listUrl, String bookName) {
        Request request = new Request(listUrl);
        request.setCharSet("GBK");
        request.putDateMap("proxy", getProxy(host1));
        int retrye = 0;
        while (retrye < 2) {
            retrye++;
            Page page = HttpClientDownloader.download(request);
            if (page != null && page.getStatusCode() == 302) {
                return (String) page.getExp("Location");
            } else if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                Elements elements = Xsoup.select(page.getRawText(), "//tr/@id='nr']").getElements();
                if (elements != null && (!elements.isEmpty())) {
                    for (Element element : elements) {
                        String titleName = Xsoup.select(element, "//td[1]/a/text()").get();
                        String url = Xsoup.select(element, "//td[1]/a/@href").get();
                        if (bookName.equals(titleName)) {
                            return url;
                        }
                    }
                } else {
                    // TODO 没有搜索到结果
                }


            }
        }
        return null;
    }


    /**
     * 下载并解析详情页
     * @param chapter
     */
    private void handleguibuyuorgindexDetailPage(Chapter chapter) {
        Chapter oneByBookIdAndTitle = chapterDao.findOneByBookIdAndTitle(chapter.getBookId(), chapter.getName());
        if (oneByBookIdAndTitle != null) {
            return;
        }
        try {
            Request request = new Request(chapter.getUrl());
            request.setCharSet("GBK");
            request.putDateMap("proxy", getProxy(host1));
            int retrye = 0;
            while (retrye < 6) {
                retrye++;
                Page page = HttpClientDownloader.download(request);
                Thread.sleep(8000);
                if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                    String detailContent = Xsoup.select(page.getRawText(), "//div[@id='content']").get();
                    String uuid = IdUtils.getUUID();
                    String filePath = MessageFormat.format(FileUtils.DIR_PATH,chapter.getBookId())+"/"+uuid ;
                    if (StringUtils.isNotBlank(detailContent)) {
                        FileUtils.saveContentToFile(detailContent,filePath);
                    }
                    chapter.setFilePath(filePath);
                    chapter.setCreateTime(new Date());
                    chapter.setUpdateTime(new Date());
                    chapter.setId(uuid);
                    chapterDao.saveChapter(chapter);
                    logger.info("正文储存成功,title:{}",chapter.getName());
                    return;
                }
            }
            logger.info("页面下载失败,title:{},url:{}",chapter.getName(),chapter.getUrl());
        } catch (Exception e) {
            logger.error("正文解析储存出错",e);
        }
    }

    /**
     * 获取详情页的链接
     *
     * @param searcUrl
     * @return
     */
    private List<Chapter> getguibuyuorgindexList(String searcUrl, String bookName) {
        // 得到真实列表页的链接
        String listUrl = getguibuyuorgindexListUrl(searcUrl, bookName);
        if (StringUtils.isBlank(listUrl)) {
            return null;
        }
        Request request = new Request(listUrl);
        request.setCharSet("GBK");
        request.putDateMap("proxy", getProxy(host1));

        int retrye = 0;
        while (retrye < 6) {
            retrye++;
            // 下载列表页  获得所有详情页链接
            Page page = HttpClientDownloader.download(request);
            if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                String listDiv = Xsoup.select(page.getRawText(), "//div[@id='list']/dl").get();
                // 得到作者
                String author = Xsoup.select(page.getRawText(), "//div[@id='info']/p[1]/text()").get();
                Book book1 = bookDao.fondOneByNameAndAuthor(bookName, author);
                String bookId ;
                if (book1 != null) {
                    bookId = book1.getId();
                    bookDao.updateBoodById(new Date(),bookId);
                } else {
                    // 得到简介
                    String intro = Xsoup.select(page.getRawText(), "//div[@id='intro']/p[1]/text()").get();
                    bookId =  IdUtils.getUUID();
                    Book book = new Book();
                    book.setId(bookId);
                    book.setUrl(listUrl);
                    book.setAuthor(MStringUtils.trim(author));
                    book.setIntroduction(MStringUtils.trim(intro));
                    book.setCreateTime(new Date());
                    book.setUpdateTime(new Date());
                    book.setName(bookName);
                    String dirPath = MessageFormat.format(FileUtils.DIR_PATH, bookId);
                    FileUtils.chechFileDir(dirPath);
                    bookDao.saveBook(book);
                }
                Pattern pattern = Pattern.compile("正文\\s*</dt>([\\s\\S]*)</dl>");
                Matcher matcher = pattern.matcher(listDiv);
                if (matcher.find()) {
                    String listDds = matcher.group(1);
                    List<String> listUrls = Xsoup.select(listDds, "//a/@href").list();
                    List<String> titles = Xsoup.select(listDds, "//a/text()").list();
                    List<Chapter> chapters = new ArrayList<>();
                    String substring1 = listUrl.substring(0, listUrl.lastIndexOf("/"));
                    String substring = substring1.substring(0, substring1.lastIndexOf("/"));
                    if (listUrls != null && (!listUrls.isEmpty())) {
                        int count = 1;
                        for (int i = 0; i < listUrls.size(); i++) {
                            Chapter chapter = new Chapter();
                            chapter.setUrl(substring + listUrls.get(i));
                            chapter.setCount(count);
                            chapter.setName(titles.get(i));
                            chapter.setBookId(bookId);
                            chapters.add(chapter);
                            count++;
                        }
                    }
                    listUrls = null;
                    return chapters;
                }
                break;
            }
        }
        return null;
    }


    /**
     * 获取真实详情页的链接
     *
     * @param listUrl
     * @return
     */
    private String getguibuyuorgindexListUrl(String listUrl, String bookName) {
        Request request = new Request(listUrl);
        request.setCharSet("GBK");
        request.putDateMap("proxy", getProxy(host1));
        int retrye = 0;
        while (retrye < 2) {
            retrye++;
            Page page = HttpClientDownloader.download(request);
            if (page != null && page.getStatusCode() == 302) {
                return (String) page.getExp("Location");
            } else if (page != null && page.getStatusCode() == 200 && StringUtils.isNotBlank(page.getRawText())) {
                Elements elements = Xsoup.select(page.getRawText(), "//tr/@id='nr']").getElements();
                if (elements != null && (!elements.isEmpty())) {
                    for (Element element : elements) {
                        String titleName = Xsoup.select(element, "//td[1]/a/text()").get();
                        String url = Xsoup.select(element, "//td[1]/a/@href").get();
                        if (bookName.equals(titleName)) {
                            return url;
                        }
                    }
                } else {
                    // TODO 没有搜索到结果
                }


            }
        }
        return null;
    }



}
