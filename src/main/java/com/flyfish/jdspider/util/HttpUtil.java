package com.flyfish.jdspider.util;

import com.ning.http.client.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.http.HttpClient;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by laiqifan on 2017/8/16.
 */
public class HttpUtil {


    final static AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();

    //doget 同步
    //dopost 异步
    //可以直接拿到client
    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);
    static AsyncHttpClient client;

    static {
        builder.setMaxConnections(3000);
        builder.setConnectTimeout(15000);
        builder.setRequestTimeout(300000);
        builder.setReadTimeout(900000);
        builder.setMaxConnectionsPerHost(200);
        client = new AsyncHttpClient(builder.build());

    }

    public static String httpGet(String url) {
        return httpGet(url, null, null, null);
    }

    public static String httpGet(String url, String proxyHost, Integer proxyPort) {
        return httpGet(url, null, proxyHost, proxyPort);
    }

    public static String httpGet(String url, Map<String, String> queryParam) {
        return httpGet(url, queryParam, null, null);
    }

    public static void httpPostAsync(String url, Map<String, String> param, String proxyHost, Integer proxyPort, AsyncCompletionHandler<String> handler) {
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        if (!StringUtils.isBlank(proxyHost) && proxyPort != null) {
            builder.setProxyServer(new ProxyServer(ProxyServer.Protocol.HTTP, proxyHost, proxyPort));
        }
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> e : param.entrySet()) {
                builder.addFormParam(e.getKey(), e.getValue());
            }
        }
        builder.execute(handler);
    }

    public static void httpGetAsync(String url, String proxyHost, Integer proxyPort, AsyncCompletionHandler<String> handler) {
        logger.info("[httpGetAsync]: " + url);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        if (!StringUtils.isBlank(proxyHost) && proxyPort != null) {
            builder.setProxyServer(new ProxyServer(ProxyServer.Protocol.HTTP, proxyHost, proxyPort));
        }
        builder.execute(handler);
    }

    public static String httpPost(String url, Map<String, String> param) {
        return httpPost(url,param,null,null,null);
    }

    public static String httpPost(String url, Map<String, String> param, String body) {
        return httpPost(url, param, body, null, null);
    }

    public static String httpPost(String url, String body) {
        return httpPost(url,null,body,null,null);
    }

    public static String httpPost(String url, Map<String, String> param, String proxyHost, Integer proxyPort) {
        return httpPost(url,param,null,proxyHost,proxyPort);
    }

    public static String httpPost(String url, String body, String proxyHost, Integer proxyPort) {
        return httpPost(url,null,body,proxyHost,proxyPort);
    }

    public static String httpGet(String url, Map<String, String> queryParam, Map<String, String> headers) {
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.setHeader(header.getKey(), header.getValue());
            }
        }
        return doHttp(builder, queryParam, null, null, null);
    }

    public static String httpGet(String url, Map<String, String> queryParam, String proxyHost, Integer proxyPort) {
        logger.info("HttpClient.httpGet.url:" + url);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        return doHttp(builder, queryParam, null, proxyHost, proxyPort);
    }

    public static byte[] httpGetAsBytes(String url, Map<String, String> queryParam, Map<String, String> headers) {
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.setHeader(header.getKey(), header.getValue());
            }
        }
        return doHttpAsBytes(builder, queryParam, null, null, null);
    }

    public static byte[] httpGetAsBytes(String url, Map<String, String> queryParam, String proxyHost, Integer proxyPort) {
        logger.info("HttpClient.httpGet.url:" + url);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        return doHttpAsBytes(builder, queryParam, null, proxyHost, proxyPort);
    }

    public static String httpPost(String url, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        logger.info("HttpClient.httpPost.url:" + url);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        return doHttp(builder, param, body, proxyHost, proxyPort);
    }

    public static ListenableFuture<String> httpAsyncPost(String url, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        return doHttp0(builder, param, body, proxyHost, proxyPort);
    }

    private static byte[] doHttpAsBytes(AsyncHttpClient.BoundRequestBuilder builder, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        try {
            logger.info("HttpClient.doHttp.body:" + body);
            if (param != null) {
                Set<String> key = param.keySet();
                for (Iterator it = key.iterator(); it.hasNext(); ) {
                    String s = (String) it.next();
                    logger.info("HttpClient.doHttp.map.key:" + s + ";value:" + param.get(s));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HttpClient.doHttp.Exception: ", e);
        }

        try {
            return doHttp0AsBytes(builder, param, body, proxyHost, proxyPort).get(300, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("http请求异常" + e);
            return null;
        }
    }

    private static String doHttp(AsyncHttpClient.BoundRequestBuilder builder, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        try {
            logger.info("HttpClient.doHttp.body:" + body);
            if (param != null) {
                Set<String> key = param.keySet();
                for (Iterator it = key.iterator(); it.hasNext(); ) {
                    String s = (String) it.next();
                    logger.info("HttpClient.doHttp.map.key:" + s + ";value:" + param.get(s));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("HttpClient.doHttp.Exception: ", e);
        }

        try {
            return doHttp0(builder, param, body, proxyHost, proxyPort).get(300, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("http请求异常" + e);
            return null;
        }
    }

    private static ListenableFuture<byte[]> doHttp0AsBytes(AsyncHttpClient.BoundRequestBuilder builder, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        if (!StringUtils.isBlank(proxyHost) && proxyPort != null) {
            builder.setProxyServer(new ProxyServer(ProxyServer.Protocol.HTTP, proxyHost, proxyPort));
        }
        builder.setFollowRedirects(true);

        if (!StringUtils.isBlank(body)) {
            builder.setBody(body);
        }
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> e : param.entrySet()) {
                if (StringUtils.isBlank(body)) {
                    builder.addFormParam(e.getKey(), e.getValue());
                } else {
                    builder.addQueryParam(e.getKey(), e.getValue());
                }
            }
        }
        return builder.execute(new AsyncCompletionHandler<byte[]>() {
            @Override
            public byte[] onCompleted(Response response) throws Exception {
                if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                    throw new IllegalStateException("error http request code:" + response.getStatusCode() + ", text:" + response.getStatusText());
                }
                return response.getResponseBodyAsBytes();
            }
        });
    }

    private static ListenableFuture<String> doHttp0(AsyncHttpClient.BoundRequestBuilder builder, Map<String, String> param, String body, String proxyHost, Integer proxyPort) {
        if (!StringUtils.isBlank(proxyHost) && proxyPort != null) {
            builder.setProxyServer(new ProxyServer(ProxyServer.Protocol.HTTP, proxyHost, proxyPort));
        }
        builder.setFollowRedirects(true);

        if (!StringUtils.isBlank(body)) {
            builder.setBody(body);
        }
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> e : param.entrySet()) {
                if (StringUtils.isBlank(body)) {
                    builder.addFormParam(e.getKey(), e.getValue());
                } else {
                    builder.addQueryParam(e.getKey(), e.getValue());
                }
            }
        }
        return builder.execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) throws Exception {
                if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                    throw new IllegalStateException("error http request code:" + response.getStatusCode() + ", text:" + response.getStatusText());
                }
                return response.getResponseBody();
            }
        });
    }

    public static String HttpPostJson(String url, String body, String proxyHost, Integer proxyPort) {
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.setHeader("Content-Type", "application/json");
        builder.setBodyEncoding("utf-8");
        return doHttp(builder, null, body, proxyHost, proxyPort);
    }

    public static String HttpPostJson(String url, Map<String, String> param, String body, String proxyHost, Integer
        proxyPort) {
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.setHeader("Content-Type", "application/json");
        builder.setBodyEncoding("utf-8");
        return doHttp(builder, param, body, proxyHost, proxyPort);
    }

    public static String httpPostForm(String url, Map<String, String> param, String proxyHost, Integer proxyPort) {
        logger.info("HttpClient.httpPost.url:" + url);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBodyEncoding("utf-8");
        return doHttp(builder, param, null, proxyHost, proxyPort);
    }

    public static String TDHttpPost(String url, String body, String proxyHost, Integer proxyPort) {
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBodyEncoding("utf-8");
        return doHttp(builder, null, body, proxyHost, proxyPort);
    }

}
