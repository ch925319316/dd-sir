package com.web.mundo.service;

import com.web.mundo.util.HttpClientDownloader;
import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chenhao
 * @title: ProxyService
 * @projectName mundo
 * @description: 代理池
 * @date 2019/12/22  16:56
 */
@Service
@Scope("singleton")
@EnableScheduling
public class ProxyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyService.class);
    private static final List<ProxyVO> PROXY_LIST = new ArrayList<>();
    private static final Map<String, List<ProxyVO>> MIN_PROXY_LIST = new HashMap<>();

    private static final String PROXY_URL = "http://hzdc2019.v4.dailiyun.com/query.txt?key=NP41F7D02D&word=&count=10&rand=false&detail=true";

    public static  ProxyVO getProxy(String host, int validTime, int lockTime) {
        synchronized(MIN_PROXY_LIST){
            try {
                initMINProxys(host);
                List<ProxyVO> proxyVOS = MIN_PROXY_LIST.get(host);
                if (proxyVOS == null || proxyVOS.isEmpty()) {
                    return null;
                }
                for (int i = 0; i < proxyVOS.size(); i++) {
                    ProxyVO vo = proxyVOS.get(i);
                    Long dateLong = vo.getValidDate();
                    if (dateLong - System.currentTimeMillis() > (validTime * 60000)) {//剩余有效时间大于1min
                        Long lockDate = vo.getLockDate();
                        if (lockDate == null || lockDate == 0 || System.currentTimeMillis() > lockDate) {// 验证锁定时间
                            vo.setLockDate(System.currentTimeMillis() + ((lockTime + 1) * 1000));
                            proxyVOS.remove(i);
                            proxyVOS.add(vo);
                            return vo;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("出错了", e);
            }
            LOGGER.info("未获取到有效代理");
            return null;
        }
    }


   @Scheduled(fixedDelay = 30000)
    private void taskUpdateProxy(){
        Request request = new Request(PROXY_URL);
       request.setNoUseProxy(true);
        Page page = HttpClientDownloader.download(request);
        if (page != null && StringUtils.isNotBlank(page.getRawText())) {
            String iplist = page.getRawText();
            String[] ipssplit = iplist.split("\r\n");
            PROXY_LIST.clear();
            for (String ipString : ipssplit) {
                if (StringUtils.isNotBlank(ipString)) {
                    PROXY_LIST.add(executeProxy(ipString.trim()));
                }
            }
            for (ProxyVO pp : PROXY_LIST) {
                Set<String> keySet = MIN_PROXY_LIST.keySet();
                for (String s1 : keySet) {
                    List<ProxyVO> proxyVOS = MIN_PROXY_LIST.get(s1);
                    if (!proxyVOS.contains(pp)) {
                        proxyVOS.add(pp);
                    }
                }
            }
            LOGGER.info("代理更新成功...,可用代理个数:{}",PROXY_LIST.size());
        }
    }

    private ProxyVO executeProxy(String ipString) {
        ProxyVO vo= new ProxyVO();
        String validDateString = ipString.split(",")[4];
        String ipDataString = ipString.split(",")[0].split(":")[0];
        String hostString = ipString.split(",")[0].split(":")[1];
        vo.setIp(ipDataString);
        vo.setValidDate(Long.parseLong((validDateString+"000")));
        vo.setPort(Integer.parseInt(hostString));
        return vo;
    }

    private static  void initMINProxys(String host){
        List<ProxyVO> proxyVOS = MIN_PROXY_LIST.get(host);
        // 此host第一次下载  初始化一个小代理池
        if (proxyVOS == null || proxyVOS.isEmpty()) {
            proxyVOS = new ArrayList<>();
            proxyVOS.addAll(PROXY_LIST);
            MIN_PROXY_LIST.put(host,proxyVOS );
        } else {
            // 代理池已存在  检查超时的代理
            List<ProxyVO> temList = new ArrayList<>();
            for (ProxyVO proxyVO : proxyVOS) {
                if (checkValiDate(proxyVO)) {
                    // 代理可用
                    temList.add(proxyVO);
                }
            }
            proxyVOS.clear();
            MIN_PROXY_LIST.put(host,temList);
        }
    }
    private static boolean checkValiDate(ProxyVO vo){
        if (vo.getValidDate() > System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

}
