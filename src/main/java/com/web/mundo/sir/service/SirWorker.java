package com.web.mundo.sir.service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.web.mundo.sir.dao.ISirDao;
import com.web.mundo.sir.po.SirTs;
import com.web.mundo.sir.po.SirVideo;
import com.web.mundo.util.HttpClientDownloader;
import com.web.mundo.util.JsonPathUtils;
import com.web.mundo.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.web.mundo.config.RequestMenthodEnum;
import com.web.mundo.sir.util.EncryptManager;
import com.web.mundo.vo.Request;

@Service
public class SirWorker {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Gson gson = new Gson();
    
    public static EncryptManager encryptManager  = EncryptManager.getInstance();

    @Autowired
    ISirDao sirDao;

    public void saveTsById(String vid, String oauth_id) {
        SirVideo video = sirDao.findVideo(vid);
        if (video != null && ("1".equals(video.getIs_down()) || "2".equals(video.getIs_down()))) {
            logger.info("已存在下载，vid:{}", vid);
            return;
        }
        String mv_url = video.getMv_url();
        Request request = new Request(mv_url);
        Map<String, String> heards = new HashMap<>();
        heards.put("User-Agent", "dd");
        request.setHeards(heards);
        Page download = HttpClientDownloader.download(request);
        if (download == null) {
            logger.info("下载失败,vid:{}", vid);
            video.setIs_down("3");
        }
        video.setIs_down("4");
        video.setUpdate_time(new Date());
        video.setMv_url(null);
        sirDao.updateVideo(video);
        String tsResult = new String(download.getResource());
        Pattern pattern = Pattern.compile("\\s+(http.+?auth_key=.+?)\\s+");
        Matcher matcher = pattern.matcher(tsResult);
        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        List<SirTs> tsList = sirDao.findTsList(vid);
        for (int i = 0; i < urls.size(); i++) {
            SirTs sirTs = new SirTs();
            sirTs.setCount(String.valueOf(i));
            sirTs.setV_id(vid);
            sirTs.setCreate_time(new Date());
            sirTs.setUpdate_time(new Date());
            sirTs.setUrl(urls.get(i));
            if (tsList == null || tsList.isEmpty()) {
                sirDao.saveTs(sirTs);
                continue;
            }
            boolean flag = true;
            for (SirTs ts : tsList) {
                if (String.valueOf(i).equals(ts.getCount())) {
                    if ("0".equals(ts.getIs_down())) {
                        sirTs.setId(ts.getId());
                        sirDao.updateTs(sirTs);
                    }
                    flag = false;
                    break;
                }
            }
            if (flag) {
                sirDao.saveTs(sirTs);
            }
        }
    }


    public void startDetailById(String vid, String oauth_id) {
        SirVideo video = sirDao.findVideo(vid);
        if (video != null && ("1".equals(video.getIs_down()) || "2".equals(video.getIs_down()))) {
            logger.info("已存在下载，vid:{}", vid);
            return;
        }
        for (int i = 0; i < 3; i++) {
            String param = "{\"id\":" + vid + ",\"mod\":\"av\"," +
                    "\"code\":\"detail\",\"oauth_id\":" +
                    "\"" + oauth_id + "\",\"oauth_type\":\"android_rn\"," +
                    "\"version\":\"3.1.2\",\"os_version\":\"9\"," +
                    "\"app_type\":\"rn\",\"bundleId\":\"com.sunrise\"," +
                    "\"via\":\"agent\",\"channel\":\"self\"}";
            String requestJson = encryptManager.getRequestJson(param);
            Map fromJson = gson.fromJson(requestJson, Map.class);
            Set keySet = fromJson.keySet();
            String data = "";
            for (Object object : keySet) {
                data = data + object + "=" + fromJson.get(object) + "&";
            }
            data = data.substring(0, data.length() - 1);
            Map<String, String> heards = new HashMap<>();
            heards.put("User-Agent", "okhttp/3.9.1");
            heards.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            Request request = new Request();
            request.setUrl("http://sir_new.hitik.net:8080/api.php?t=" + System.currentTimeMillis());
            request.setRequestMenthod(RequestMenthodEnum.POST);
            request.setPostData(data);
            request.setHeards(heards);
            Page download = HttpClientDownloader.download(request);
            if (download == null || StringUtils.isEmpty(download.getRawText())) {
                logger.info("下载失败,vid:{}", vid);
                continue;
            }
            EncryptManager encryptManager = EncryptManager.getInstance();
            String reusltJson = encryptManager.getReusltJson(download.getRawText());
            List<LinkedHashMap> lists = JsonPathUtils.readListMap(reusltJson, "$.line[*]");
            if (lists == null) {
                logger.info("未解析出 video url ，count:{},vid:{},result:{}", i, vid, reusltJson);
                continue;
            }
            LinkedHashMap linkedHashMap = JsonPathUtils.readMap(reusltJson, "$.data");
            SirVideo sirVideo = toVideoPO(linkedHashMap);
            if (sirVideo == null) {
                logger.error("解析出错,vid:{},result:{}", vid, reusltJson);
                continue;
            }
            // 获取mv_url
            String videoUrl = JsonPathUtils.readString(lists.get(0), "$.line.s720");
            sirVideo.setMv_url(videoUrl);
            if (StringUtils.isEmpty(videoUrl)) {
                sirVideo.setIs_down("9");
            }
            if (video == null) {
                // 新增
                sirDao.saveVideo(sirVideo);
                logger.info("detail save,vid:{}", vid);
                return;
            }
            sirDao.updateVideo(sirVideo);
            logger.info("detail update,vid:{}", vid);
            return;
        }
    }


    public void startFeed(int pageCount) {
        String oauth_id = "84b124q0a79166bc";
        for (int count = 1; count < pageCount; count++) {
            String param = "{\"page\":" + count + ",\"mod\":\"av\",\"code\":\"search\",\"tag\":\"new\"," +
                    "\"oauth_id\":\"" + oauth_id + "\",\"oauth_type\":\"android_rn\"," +
                    "\"version\":\"3.1.2\",\"os_version\":\"9\",\"app_type\":\"rn\"," +
                    "\"bundleId\":\"com.sunrise\",\"via\":\"agent\",\"channel\":\"self\"}";
            String requestJson = encryptManager.getRequestJson(param);
            Map fromJson = gson.fromJson(requestJson, Map.class);
            Set keySet = fromJson.keySet();
            String data = "";
            for (Object object : keySet) {
                data  =  data + object+"="+fromJson.get(object) + "&";
            }
            data = data.substring(0,data.length()-1);
            Map<String, String> heards = new HashMap<>();
            heards.put("User-Agent", "okhttp/3.9.1");
            heards.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            Request request = new Request();
            request.setUrl("http://sir_new.hitik.net:8080/api.php?t="+System.currentTimeMillis());
            request.setRequestMenthod(RequestMenthodEnum.POST);
            request.setPostData(data);
            request.setHeards(heards);
            Page download = HttpClientDownloader.download(request);
            if (download == null || StringUtils.isEmpty(download.getRawText())) {
                count--;
                logger.info("下载失败,count:{}", count);
                continue;
            }
            EncryptManager encryptManager = EncryptManager.getInstance();
            String reusltJson = encryptManager.getReusltJson(download.getRawText());
            List<LinkedHashMap> lists = JsonPathUtils.readListMap(reusltJson, "$.data[*]");
            if (lists == null) {
                logger.info("本页为空,count:{}，result:{}", count, reusltJson);
                count--;
                continue;
            }
            for (LinkedHashMap hashMap : lists) {
                SirVideo sirVideo = toVideoPO(hashMap);
                if (sirVideo != null) {
                    saveOrUpdate(sirVideo);
                }
            }
            logger.info("本页:{},下载 count:{}", count, lists.size());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveOrUpdate(SirVideo sirVideo) {
        SirVideo video = sirDao.findVideo(sirVideo.getId());
        if (video != null) {
            sirDao.updateVideo(sirVideo);
//            logger.info("video update success id:{}", sirVideo.getId());
            return;
        }
        sirDao.saveVideo(sirVideo);
//        logger.info("video save success id:{}", sirVideo.getId());
    }


    private SirVideo toVideoPO(LinkedHashMap json) {
        String id = JsonPathUtils.readString(json, "$.id");
        String _id = JsonPathUtils.readString(json, "$._id");
        String good = JsonPathUtils.readString(json, "$.good");
        String created_at = JsonPathUtils.readString(json, "$.created_at");
        String tags = JsonPathUtils.readString(json, "$.tags");
        String title = JsonPathUtils.readString(json, "$.title");
        String duration = JsonPathUtils.readString(json, "$.duration");
        String cover_full = JsonPathUtils.readString(json, "$.cover_full");
        if (StringUtils.isNotBlank(cover_full)) {
            cover_full = cover_full.replaceFirst("(http.+?//.+?)(/.+)", "https://new_img.ycomesc.com$2");
        }
        String actors = JsonPathUtils.readString(json, "$.actors");
        String rating = JsonPathUtils.readString(json, "$.rating");
        String price = JsonPathUtils.readString(json, "$.price");
        String badges = JsonPathUtils.readString(json, "$.badges");

        if (StringUtils.isBlank(_id)) {
            return null;
        }

        SirVideo sirVideo = new SirVideo();
        sirVideo.setTags(tags);
        sirVideo.setId(id);
        sirVideo.set_id(_id);
        sirVideo.setGood(good);
//        sirVideo.setMv_share(mv_share);
        sirVideo.setCreated_at(created_at);
        sirVideo.setTitle(title);
        sirVideo.setDuration(duration);
        sirVideo.setCover_full(cover_full);
        sirVideo.setActors(actors);
        sirVideo.setRating(rating);
        sirVideo.setPrice(price);
        sirVideo.setBadges(badges);
        sirVideo.setCreate_time(new Date());
        sirVideo.setUpdate_time(new Date());
        return sirVideo;
    }


    private SirVideo toDetailPO(LinkedHashMap json) {
        String id = JsonPathUtils.readString(json, "$.id");
        String _id = JsonPathUtils.readString(json, "$._id");
        String good = JsonPathUtils.readString(json, "$.good");
        String created_at = JsonPathUtils.readString(json, "$.created_at");
        String tags = JsonPathUtils.readString(json, "$.tags");
        String title = JsonPathUtils.readString(json, "$.title");
        String duration = JsonPathUtils.readString(json, "$.duration");
        String cover_full = JsonPathUtils.readString(json, "$.cover_full");
        if (StringUtils.isNotBlank(cover_full)) {
            cover_full = cover_full.replaceFirst("(http.+?//.+?)(/.+)", "https://new_img.ycomesc.com$2");
        }
        String actors = JsonPathUtils.readString(json, "$.actors");
        String rating = JsonPathUtils.readString(json, "$.rating");
        String price = JsonPathUtils.readString(json, "$.price");
        String badges = JsonPathUtils.readString(json, "$.badges");

        if (StringUtils.isBlank(_id)) {
            return null;
        }

        SirVideo sirVideo = new SirVideo();
        sirVideo.setTags(tags);
        sirVideo.setId(id);
        sirVideo.set_id(_id);
        sirVideo.setGood(good);
//        sirVideo.setMv_share(mv_share);
        sirVideo.setCreated_at(created_at);
        sirVideo.setTitle(title);
        sirVideo.setDuration(duration);
        sirVideo.setCover_full(cover_full);
        sirVideo.setActors(actors);
        sirVideo.setRating(rating);
        sirVideo.setPrice(price);
        sirVideo.setBadges(badges);
        sirVideo.setCreate_time(new Date());
        sirVideo.setUpdate_time(new Date());
        return sirVideo;
    }


}
