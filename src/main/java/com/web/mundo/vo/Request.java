package com.web.mundo.vo;

import com.web.mundo.config.RequestMenthodEnum;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public Request() {
    }

    public Request(String url) {
        this.url = url;
    }

    /**
     * 下载链接url
     */
    private String url;
    /**
     * 请求方式
     */
    private RequestMenthodEnum requestMenthod = RequestMenthodEnum.GET;
    /**
     *  下载的字符编码
     */
    private String charSet;

    /**
     * 可以存储任何数据信息
     */
    private Map<String,Object> dateMap = new HashMap<>();

    /**
     * 若是post请求，存储请求参数
     */
    private Map<String,String> postParams;

    private String postData;

    private boolean noUseProxy = false;
    
    
    private Map<String,String> heards = new HashMap<String, String>();
    
    public Map<String, Object> getDateMap() {
		return dateMap;
	}

	public void setDateMap(Map<String, Object> dateMap) {
		this.dateMap = dateMap;
	}

	public Map<String, String> getHeards() {
		return heards;
	}

	public void setHeards(Map<String, String> heards) {
		this.heards = heards;
	}

	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMenthodEnum getRequestMenthod() {
        return requestMenthod;
    }

    public void setRequestMenthod(RequestMenthodEnum requestMenthod) {
        this.requestMenthod = requestMenthod;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public Object getDateMap(String key) {
        return dateMap.get(key);
    }

    public void putDateMap(String key , Object val) {
        dateMap.put(key, val);
    }

    public Map<String, String> getPostParams() {
        return postParams;
    }

    public void setPostParams(Map<String, String> postParams) {
        this.postParams = postParams;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public boolean isNoUseProxy() {
        return noUseProxy;
    }

    public void setNoUseProxy(boolean noUseProxy) {
        this.noUseProxy = noUseProxy;
    }
}
