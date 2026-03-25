package com.jcloud.system.utils;

import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http接口调用
 * @Auther: yang
 * @Date: 2022/02/17/10:42
 * @Description:
 */
public class UrlUtils {
    private static HttpClient httpClient;
    // 最大连接数
    private static final int MAX_CONNECTION = 100;
    // 每个route能使用的最大连接数，一般和MAX_CONNECTION取值一样
    private static final int MAX_CONCURRENT_CONNECTIONS = 100;
    // 建立连接的超时时间，单位毫秒
    private static final int CONNECTION_TIME_OUT = 1000;
    // 请求超时时间，单位毫秒
    private static final int REQUEST_TIME_OUT = 1000;
    // 最大失败重试次数
    private static final int MAX_FAIL_RETRY_COUNT = 3;
    // 请求配置，可以复用
    private static RequestConfig requestConfig;

    static {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(REQUEST_TIME_OUT).setSoKeepAlive(true)
                .setTcpNoDelay(true).build();

        requestConfig = RequestConfig.custom()
                .setSocketTimeout(REQUEST_TIME_OUT)
                .setConnectTimeout(CONNECTION_TIME_OUT).build();
        /**
         * 每个默认的 ClientConnectionPoolManager 实现将给每个route创建不超过2个并发连接，最多20个连接总数。
         */
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(MAX_CONNECTION);
        connManager.setDefaultMaxPerRoute(MAX_CONCURRENT_CONNECTIONS);
        connManager.setDefaultSocketConfig(socketConfig);

        httpClient = HttpClients.custom().setConnectionManager(connManager)
                // 添加重试处理器
                .setRetryHandler(new MyHttpRequestRetryHandler()).build();
    }

    public static void main(String[] args) {
        testGet();
    }

    /**
     * 测试get方法
     */
    private static void testGet() {
        String url = "http://127.0.0.1/system/comRegion/test2";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("1234", "95708f902ac2428ea119ec99fb70e6a3");
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.AUTHORIZATION, ""));
//        paramMap.put("keywords", "互联网金融大厦");
//        paramMap.put("city", "330100");
//        paramMap.put("extensions", "all");

        try {
            System.out.println(get(url, paramMap,headers));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求
     *
     * @param url
     * @param paramMap
     * @param headers
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, String> paramMap,
                              List<Header> headers) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (paramMap != null) {
            // 添加请求参数
            for (Entry<String, String> entry : paramMap.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }

        HttpPost httpPost = new HttpPost(uriBuilder.build());
        if (headers != null) {
            // 添加请求首部
            for (Header header : headers) {
                httpPost.addHeader(header);
            }
        }

        httpPost.setConfig(requestConfig);

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
    }

    public static String postWithBody(String url, String bodyData, Map<String, String> headersMap) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build(); // 创建HttpClient实例（如果需要其他配置可以在这里设置）
        HttpPost httpPost = new HttpPost(url); // 创建HttpPost请求
        StringEntity entity = new StringEntity(bodyData, Charset.forName("UTF-8")); // 创建包含数据的请求实体，并设置字符集为UTF-8
        httpPost.setEntity(entity); // 设置请求实体到HttpPost中
        httpPost.setHeader("Content-Type", "application/json");
        if (headersMap != null) {
            // 添加请求首部（如果使用Map来存储头部信息）
            for (Entry<String, String> headerEntry : headersMap.entrySet()) {
                httpPost.addHeader(new BasicHeader(headerEntry.getKey(), headerEntry.getValue())); // 添加头部信息到HttpPost请求中
            }
        }
        // 设置请求配置（如果需要的话）
        // httpPost.setConfig(...);
        HttpResponse response = httpClient.execute(httpPost); // 执行请求并获取响应
        return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8")); // 将响应实体转换为字符串并返回
    }

    /**
     * post请求，不带请求首部
     *
     * @param url
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, String> paramMap)
            throws Exception {

        return post(url, paramMap, null);
    }

    public static String postWithBody(String url, String bodyData) throws Exception {
        return postWithBody(url, bodyData, null);
    }


    /**
     * get请求
     *
     * @param url
     * @param paramMap
     * @param headers
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, String> paramMap,
                             List<Header> headers) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (paramMap != null) {
            // 添加请求参数
            for (Entry<String, String> entry : paramMap.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        if (headers != null) {
            // 添加请求首部
            for (Header header : headers) {
                httpGet.addHeader(header);
            }
        }

        httpGet.setConfig(requestConfig);

        // 执行请求
        HttpResponse response = httpClient.execute(httpGet);

        return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
    }

    /**
     * get请求，不带请求首部
     *
     * @param url
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, String> paramMap)
            throws Exception {

        return get(url, paramMap, null);
    }

    /**
     * 请求重试处理器
     *
     * @author manzhizhen
     */
    private static class MyHttpRequestRetryHandler implements
            HttpRequestRetryHandler {

        @Override
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            if (executionCount >= MAX_FAIL_RETRY_COUNT) {
                return false;
            }

            if (exception instanceof InterruptedIOException) {
                // 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // 未知主机
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                // 连接被拒绝
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // 如果请求被认为是幂等的，则重试
                return true;
            }

            return false;
        }
    }
}
