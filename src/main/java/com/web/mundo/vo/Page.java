package com.web.mundo.vo;

import java.util.HashMap;
import java.util.Map;

public class Page {

    private String rawText;

    private byte[] resource;

    /**
     * 响应状态码
     */
    private int statusCode;

    private Map<String,Object>  heards = new HashMap<>();

    public Object getExp(String key) {
        return heards.get(key);
    }

    public void putExp(String key,Object val) {
        heards.put(key, val);
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public byte[] getResource() {
        return resource;
    }

    public void setResource(byte[] resource) {
        this.resource = resource;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
