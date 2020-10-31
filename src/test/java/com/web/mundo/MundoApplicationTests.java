package com.web.mundo;

import com.web.mundo.dao.IChapterDao;
import com.web.mundo.po.Chapter;
import com.web.mundo.service.ProxyService;
import com.web.mundo.util.HttpClientDownloader;
import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import com.web.mundo.worker.TestCrawler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartBoot.class)
public class MundoApplicationTests {

    @Autowired
    TestCrawler testCrawler;

    @Autowired
    private IChapterDao chapterDao;
    @Test
    public void contextLoads() throws IOException, InterruptedException {
//        for (int i = 1; i < 1199; i++) {
//            Request request = new Request("https://p.cnkamax.com/upload18files/d1a5825bc7712ea0a10092e29ffddbff/ts/SSNI-671-"+i+".ts");
//            Page download = HttpClientDownloader.download(request);
//            byte[] resource = download.getResource();
//            if (resource != null) {
//                FileOutputStream fileOutputStream = new FileOutputStream("D:\\test\\test1.ts", true);
//                fileOutputStream.write(resource);
//                fileOutputStream.flush();
//                fileOutputStream.close();
//                System.out.println("储存成功" + i);
//                Thread.sleep(500);
//            } else {
//                return;
//            }
//        }
            testCrawler.start("酒神（阴阳冕）");
    }

        @Test
    public void testProxy() {
            while (true) {
                ProxyVO proxy = ProxyService.getProxy("asd",1, 2);
                System.out.println("***"+proxy);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


        @Test
    public void testProxy22() throws Exception {
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\酒神阴阳冕.txt",true);
            List<Chapter> chapters = chapterDao.queryAllDetails("9c7e9da038094ec085e4f263ed3e66fd");
            for (Chapter chapter : chapters) {
                String filePath = chapter.getFilePath();
                FileInputStream fileInputStream = null;
                try {
                    File file = new File(filePath);
                    byte[] bytes = new byte[(int) file.length()];
                    fileInputStream =  new FileInputStream(file);
                    fileInputStream.read(bytes);
                    fileOutputStream.write(("\n"+chapter.getName()+"\n").getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                    System.out.println("储存完成\t"+chapter.getCount()+"\t"+chapter.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (fileInputStream!=null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            File inputWord = new File("C:\\Users\\ch\\Desktop\\陈豪-Java开发简历-2.5年 - 副本.doc");
            File outputFile = new File("C:\\Users\\ch\\Desktop\\res.pdf");
            try  {
                InputStream docxInputStream = new FileInputStream(inputWord);
                OutputStream outputStream = new FileOutputStream(outputFile);
                IConverter converter = LocalConverter.builder().build();
                converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
                outputStream.close();
                System.out.println("success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testPdf() throws Exception {

            File inputWord = new File("C:\\Users\\ch\\Desktop\\陈豪-Java开发简历-2.5年 - 副本.doc");
            File outputFile = new File("C:\\Users\\ch\\Desktop\\res.pdf");
            try  {
                InputStream docxInputStream = new FileInputStream(inputWord);
                OutputStream outputStream = new FileOutputStream(outputFile);
                IConverter converter = LocalConverter.builder().build();
                IConverter converter2 = LocalConverter.builder().build();
                IConverter converter23 = LocalConverter.builder().build();
                converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
                outputStream.close();
                System.out.println("success");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


}
