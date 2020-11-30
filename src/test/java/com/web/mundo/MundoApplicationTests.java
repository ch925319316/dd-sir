package com.web.mundo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.google.gson.Gson;
import com.web.mundo.config.RequestMenthodEnum;
import com.web.mundo.dao.IChapterDao;
import com.web.mundo.po.Chapter;
import com.web.mundo.service.ProxyService;
import com.web.mundo.sir.dao.ISirDao;
import com.web.mundo.sir.po.SirVideo;
import com.web.mundo.sir.service.SirWorker;
import com.web.mundo.sir.util.EncryptManager;
import com.web.mundo.util.HttpClientDownloader;
import com.web.mundo.util.JsonPathUtils;
import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import com.web.mundo.worker.TestCrawler;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartBoot.class)
public class MundoApplicationTests {

    @Autowired
    TestCrawler testCrawler;

    @Autowired
    ISirDao sirDao;


    @Autowired
    SirWorker sirWorker;


    @Autowired
    private IChapterDao chapterDao;
    @Test
    public void contextLoads() {
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


    @Test
    public void testSaveSirVideo() throws Exception {
        Gson gson = new Gson();
        String jj = "{\n" +
                "\t\t\t\"id\": 24682,\n" +
                "\t\t\t\"_id\": \"GCAV-0209\",\n" +
                "\t\t\t\"_id_hash\": \"15185359fec097c12c95f68dfbf19f94\",\n" +
                "\t\t\t\"title\": \"[老婆爱多P]带欲求不满的老婆会网友玩3P 看着老婆被两人插真爽\",\n" +
                "\t\t\t\"source_240\": \"\",\n" +
                "\t\t\t\"source_480\": \"\",\n" +
                "\t\t\t\"source_720\": \"/upload/userupload/76e44504eb46b1cea73fa41b1d536ce0/76e44504eb46b1cea73fa41b1d536ce0.m3u8\",\n" +
                "\t\t\t\"source_1080\": \"\",\n" +
                "\t\t\t\"v_ext\": \"m3u8\",\n" +
                "\t\t\t\"duration\": 1140,\n" +
                "\t\t\t\"cover_thumb\": \"http://image.tianzeweita.com/new/av/20201031/2020103112315437161.png\",\n" +
                "\t\t\t\"cover_full\": \"http://image.tianzeweita.com/new/av/20201031/2020103112315437161.png\",\n" +
                "\t\t\t\"directors\": \"\",\n" +
                "\t\t\t\"publisher\": \"\",\n" +
                "\t\t\t\"actors\": \"素人\",\n" +
                "\t\t\t\"category\": \"\",\n" +
                "\t\t\t\"tags\": \"国产,无码,颜值,中出,美少女,体内射精,潮吹,单体作品,中文字幕\",\n" +
                "\t\t\t\"via\": \"self\",\n" +
                "\t\t\t\"is_deleted\": 0,\n" +
                "\t\t\t\"desc\": \"\",\n" +
                "\t\t\t\"onshelf_tm\": 0,\n" +
                "\t\t\t\"rating\": 234,\n" +
                "\t\t\t\"created_at\": 1604118681,\n" +
                "\t\t\t\"refresh_at\": \"2小时前\",\n" +
                "\t\t\t\"isfree\": 2,\n" +
                "\t\t\t\"dislike\": 0,\n" +
                "\t\t\t\"like\": 0,\n" +
                "\t\t\t\"price\": 990,\n" +
                "\t\t\t\"new\": true,\n" +
                "\t\t\t\"isSaved\": false,\n" +
                "\t\t\t\"badges\": [\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"name\": \"￥9.90\",\n" +
                "\t\t\t\t\t\"color\": \"#FF6347\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"mv_type\": \"av\",\n" +
                "\t\t\t\"good\": \"100%\",\n" +
                "\t\t\t\"vote_like\": false,\n" +
                "\t\t\t\"vote_dislike\": false,\n" +
                "\t\t\t\"mv_share\": \"https://invited.seyutv.net/c-1/?code=mvaff&type=av&sign=h3e&aff=ahGfE \"\n" +
                "\t\t}";
//        SirVideo sirVideo1 = gson.fromJson(jj, SirVideo.class);
//        System.out.println(sirVideo1);
        String  read = JsonPathUtils.readString(jj, "$._id");
        String  id = JsonPathUtils.readString(jj, "$.id");
        String  title = JsonPathUtils.readString(jj, "$.title");
        String  tags = JsonPathUtils.readString(jj, "$.tags");
        SirVideo sirVideo = new SirVideo();
        sirVideo.setId(id);
        sirVideo.set_id(read);
        sirVideo.setTitle(title);
        sirVideo.setTags(tags);
        sirDao.saveVideo(sirVideo);
    }


    @Test
    public void downFeed() {
        Map<String, String> heards = new HashMap<>();
        heards.put("User-Agent", "okhttp/3.9.1");
        heards.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        Request request = new Request();
        request.setUrl("http://sir_new.hitik.net:8080/api.php?t="+System.currentTimeMillis());
        request.setRequestMenthod(RequestMenthodEnum.POST);
        request.setPostData("data=44293220584B5625ACA69E18F1A4ACDFE15586701F832D39751317A435982BA4A7FF428734DF095CB42FA8D8E7E8992C284A43056792A2FD99FA46F068C4591C32AB8A7AADE201284CEEF93D41B7B5C20EF25C07DA251440A53199958B243E3A5D34D9F56420E1D51B73C4B2D457C8D7EC1ADB456D181F4E941BCB5E5E8488A6C7967A2647867047569C7B307E2C55CECA1461B43D95EA406D73B99E022F57C1C638204211794E215C655191633862B802D2A8FDD3AD3FADBE445B91FA9D11742B820C7136940F083FBDE7AB1261FBC2DA9081EE9B731803E91FD49A227558100D01461E&sign=4af80ad2683a82aaecfd1a0dcb57b0bf&timestamp=1604726122");
        request.setHeards(heards);
        Page download = HttpClientDownloader.download(request);
        System.out.println(download.getRawText());
        EncryptManager encryptManager = EncryptManager.getInstance();
        String reusltJson = encryptManager.getReusltJson(download.getRawText());
        System.out.println(reusltJson);
        List<LinkedHashMap> lists = JsonPathUtils.readListMap(reusltJson, "$.data[*]");
        System.out.println(lists);
//        for (LinkedHashMap list : lists) {
//            System.out.println(list);
//        }
    }

    @Test
    public void downSirVideo() {
        sirWorker.startFeed(10000, "84b124q0a79122bc");
    }


    @Test
    public void downSirVideoDetail() {
        String vid = "24676";
        String oauth_id = "84b59f40a79166bc";
        sirWorker.startDetailById(vid, oauth_id);
    }


    @Test
    public void saveTsById() {
        String vid = "24676";
        String oauth_id = "84b59f40a7916611";
        sirWorker.saveTsById(vid);
    }


    @Test
    public void testStart() {
        String vid = "24676";
        sirWorker.start(vid);
    }
    
    @Test
    public void testDownAll() {
        List<SirVideo> videoToDown = sirDao.findVideoToDown();
        for (SirVideo sirVideo : videoToDown) {
            sirWorker.start(sirVideo.getId());
		}

    }
    


}
