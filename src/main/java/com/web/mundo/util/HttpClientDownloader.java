package com.web.mundo.util;

import com.web.mundo.service.ProxyService;
import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * 下载器工具类
 */
public class HttpClientDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientDownloader.class);

    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager ;

    static {
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(200);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setSoTimeout(6000).setTcpNoDelay(true).setSoReuseAddress(true).setSoLinger(60).build();
        poolingHttpClientConnectionManager.setDefaultSocketConfig(socketConfig);
    }


    public static Page download(Request req){
        HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).setRetryHandler(requestRetryHandler).build();
        CloseableHttpResponse response = null;
        try {
            HttpUriRequest request = buildRequest(req);
            response =  httpClient.execute(request);
            Page page = handlePage(response, req);
            return page;
        } catch (Exception e) {
            LOGGER.error("download error,url:{}",req.getUrl(),e);
            return null;
        }finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("close response error",e);
                }
            }
        }
    }


    private static Page handlePage(CloseableHttpResponse response,Request req) throws Exception {
        Page page = new Page();
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        page.setStatusCode(statusCode);
        if (statusCode == 200) {
            Header contentType = entity.getContentType();
            String value = contentType.getValue();
            if (StringUtils.isNotBlank(value) && (value.contains("video") || value.contains("octet-stream"))) {
                byte[] bytes = EntityUtils.toByteArray(entity);
                page.setResource(bytes);
            } else {
                String content;
                if (StringUtils.isNoneBlank(req.getCharSet())) {
                    content = EntityUtils.toString(entity, req.getCharSet());
                } else {
                    content = EntityUtils.toString(entity, "utf-8");
                }
                page.setRawText(content);
            }
        }
        if (statusCode == 302) {
            Header[] allHeaders = response.getAllHeaders();
            for (Header allHeader : allHeaders) {
                if ("Location".equals(allHeader.getName()) || "location".equalsIgnoreCase(allHeader.getName())) {
                    page.putExp("Location",allHeader.getValue());
                }
            }
        }
        return page;
    }


    private static HttpUriRequest buildRequest(Request req){
        RequestBuilder requestBuilder = null;
        Map<String, String> heards = req.getHeards();
        String contentType = null;
        if (heards != null) {
            if (StringUtils.isNotBlank(heards.get("content-type"))) {
                contentType = heards.get("content-type");
            }
            if (StringUtils.isNotBlank(heards.get("Content-Type"))) {
                contentType = heards.get("Content-Type");
            }
        }
        switch (req.getRequestMenthod()) {
            case GET:
                requestBuilder =  RequestBuilder.get(req.getUrl());
                break;
            case POST:
                requestBuilder = RequestBuilder.post(req.getUrl());
                Map<String, String> postParams = req.getPostParams();
                if (postParams != null && !postParams.isEmpty()) {
                    if (StringUtils.isNotBlank(contentType) && contentType.contains("json")) {

//                        requestBuilder.setEntity(new StringEntity(params, StandardCharsets.UTF_8));
                    } else {
                        LinkedList<NameValuePair> params = new LinkedList<>();
                        Set<String> names = postParams.keySet();
                        for (String name : names) {
                            params.add(new BasicNameValuePair(name, postParams.get(name)));
                        }
                        requestBuilder.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
                    }
                } else {
                    if (StringUtils.isNotBlank(req.getPostData())) {
                        requestBuilder.setEntity(new StringEntity(req.getPostData(), StandardCharsets.UTF_8));
                    }
                }
                break;
            default:
                break;
        }
        RequestConfig.Builder builder = RequestConfig.custom().setRedirectsEnabled(false).setConnectTimeout(6000).setConnectionRequestTimeout(6000).setSocketTimeout(6000);
        if (req.isNoUseProxy()) {
//            LOGGER.info("未开启代理,url:{}",req.getUrl());
        } else {
            ProxyVO proxy = getProxy(getHost(req.getUrl()), 1, 4);
            if (proxy != null) {
                builder.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()));
//                LOGGER.info("开启代理,ip:{},port:{},url:{}",proxy.getIp(),proxy.getPort(),req.getUrl());
            } else {
//                LOGGER.info("未开启代理,url:{}",req.getUrl());
            }
        }

        RequestConfig config = builder.build();
        if (heards!=null && (!heards.isEmpty())) {
        	for (String headKey : heards.keySet()) {
        		requestBuilder.addHeader(headKey,heards.get(headKey));
			}
        }else {
        	requestBuilder.addHeader("User-Agent",
        			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        }
        return requestBuilder.setConfig(config).build();
    }

    /**
     * 获得一个代理对象
     * @param host
     * @return
     */
    private static ProxyVO getProxy(String host, int validTime, int lockTime){
        ProxyVO proxy = null;
        while(proxy == null){
            proxy = ProxyService.getProxy(host, validTime, lockTime);
            if (proxy == null) {
                try {
                    LOGGER.info("等待有效代理返回。。。");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return proxy;
    }


    public static String getHost(String link) {
        URL url;
        String host = "no";
        try {
            url = new URL(link);
            host = url.getHost();
        } catch (Exception e) {
        }
        return host;
    }

}
