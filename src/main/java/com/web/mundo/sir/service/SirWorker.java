package com.web.mundo.sir.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.web.mundo.config.RequestMenthodEnum;
import com.web.mundo.service.ProxyService;
import com.web.mundo.sir.util.EncryptManager;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;

@Service
public class SirWorker {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static String p1 = "74029765cfeaf8dd791322dfd24691b4";
    private static String p2 = "0d27dfacef1338483561a46b246bf36d";
    private static Gson gson = new Gson();
    
    public static EncryptManager encryptManager  = EncryptManager.getInstance();

    static {
    	encryptManager.init(p1, p2);
    }
    
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
    
    public void test1() {
    	int count = 1;
    	String param = "{\"page\":"+count+",\"mod\":\"av\",\"code\":\"search\",\"tag\":\"new\",\"oauth_id\":\"84b59f40a79976bc\",\"oauth_type\":\"android_rn\",\"version\":\"3.1.2\",\"os_version\":\"9\",\"app_type\":\"rn\",\"bundleId\":\"com.sunrise\",\"via\":\"agent\",\"channel\":\"self\"}";
    	String requestJson = encryptManager.getRequestJson(param);
    	Map fromJson = gson.fromJson(requestJson, Map.class);
    	Set keySet = fromJson.keySet();
    	String data = "";
    	for (Object object : keySet) {
    		data  =  data + object+"="+fromJson.get(object) + "&";
		}
    	data = data.substring(0,data.length()-1);
    	System.out.println(data);
    	
    	Request req = new Request("http://lu_new.hitik.net/api.php?t=1604160815033");
    	req.setRequestMenthod(RequestMenthodEnum.POST);
    	
    	Map<String,String> heards = new HashMap<String, String>();
    	heards.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
    	heards.put("User-Agent","okhttp/3.9.1");
    	req.setHeards(heards);
//    	POST http://lu_new.hitik.net/api.php?t=1604160815033 HTTP/1.1
//    		accept: application/json, text/plain, */*
//    		Content-Type: 
//    		Content-Length: 520
//    		Host: lu_new.hitik.net
//    		Connection: Keep-Alive
//    		Accept-Encoding: gzip
//    		Cookie: __cfduid=dd38a88871a62457e5a8c7e385fe683fb1604118903
//    		User-Agent: okhttp/3.9.1

    	
    	
    	
    }
	

}
