package com.web.mundo.util;

import com.web.mundo.vo.Page;
import com.web.mundo.vo.ProxyVO;
import com.web.mundo.vo.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

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
            if (StringUtils.isNotBlank(value) && value.contains("video")) {
                byte[] bytes = EntityUtils.toByteArray(entity);
                page.setResource(bytes);
            } else {
                String content ;
                if (StringUtils.isNoneBlank(req.getCharSet())) {
                    content = EntityUtils.toString(entity, req.getCharSet());
                } else {
                    content = EntityUtils.toString(entity,"utf-8");
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
        switch (req.getRequestMenthod()) {
            case GET:
                requestBuilder =  RequestBuilder.get(req.getUrl());
                break;
            case POST:
            	RequestBuilder.post(req.getUrl());
//            	.setEntity(entity);
                break;
            default:
                break;
        }
        RequestConfig.Builder builder = RequestConfig.custom().setRedirectsEnabled(false).setConnectTimeout(6000).setConnectionRequestTimeout(6000).setSocketTimeout(6000);
        ProxyVO proxy = (ProxyVO) req.getDateMap("proxy");
        if (proxy != null) {
            builder.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()));
            LOGGER.info("开启代理,ip:{},port:{},url:{}",proxy.getIp(),proxy.getPort(),req.getUrl());
        } else {
            LOGGER.info("未开启代理,url:{}",req.getUrl());
        }
        RequestConfig config = builder.build();
        Map<String, String> heards = req.getHeards();
        if (heards!=null && (!heards.isEmpty())) {
        	for (String headKey : heards.keySet()) {
        		requestBuilder.addHeader(headKey,heards.get(headKey));
			}
        }else {
        	requestBuilder.addHeader("User-Agent",
        			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        }
        return   requestBuilder.setConfig(config).build();
    }

}
