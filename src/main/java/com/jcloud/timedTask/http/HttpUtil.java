package com.jcloud.timedTask.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * @author banana
 * @date 2020/9/10
 * @time 14:49
 */
public class HttpUtil {



    public static final CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
    // 请求参数配置
    public static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(300000) //连接超时时间
            .setConnectionRequestTimeout(300000) //请求超时时间
            .setSocketTimeout(300000) //socket读写超时时间
            .setRedirectsEnabled(true) //是否允许重定向
            .build();

    public static ResultBean sendRequest(String url, String method, Map<String, String> params, Map<String, String> header) throws Exception {
        header.put("charset", "utf-8");
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        ResultBean resultBean = new ResultBean();
        if (null != method) {
            if (method.equalsIgnoreCase("get")) {
                resultBean = get(url, params, header);
            }
            if (method.equalsIgnoreCase("post")) {
                resultBean = post(url, params, header);
            }
        }
        return resultBean;
    }

    public static ResultBean post(String url, String params, Map<String, String> header) throws Exception {
        header.put("charset", "utf-8");
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        ResultBean resultBean = new ResultBean();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //设置参数
//        List<NameValuePair> nvps = new ArrayList<>();
//        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext(); ) {
//            String name = iter.next();
//            String value = String.valueOf(params.get(name));
//            nvps.add(new BasicNameValuePair(name, value));
//        }
        StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // 请求头设置
        if (null != header && header.size() > 0) {
            header.keySet().forEach(key -> {
                httpPost.setHeader(key, header.get(key));
            });
        }
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            resultBean.setStatusLine(response.getStatusLine());
            resultBean.setHeaders(Arrays.toString(response.getAllHeaders()));
            HttpEntity httpEntity = response.getEntity();
            resultBean.setHttpEntity(EntityUtils.toString(httpEntity));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return resultBean;
    }

    public static ResultBean post(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        if (header == null) throw new Exception("请求头不能为空");
        header.put("charset", "utf-8");
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        ResultBean resultBean = new ResultBean();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        //设置参数
//        List<NameValuePair> nvps = new ArrayList<>();
//        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext(); ) {
//            String name = iter.next();
//            String value = String.valueOf(params.get(name));
//            nvps.add(new BasicNameValuePair(name, value));
//        }
        StringEntity entity = new StringEntity(JSONObject.toJSONString(params), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // 请求头设置
        if (null != header && header.size() > 0) {
            header.keySet().forEach(key -> {
                httpPost.setHeader(key, header.get(key));
            });
        }
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpPost);
            resultBean.setStatusLine(response.getStatusLine());
            resultBean.setHeaders(Arrays.toString(response.getAllHeaders()));
            HttpEntity httpEntity = response.getEntity();
            resultBean.setHttpEntity(EntityUtils.toString(httpEntity));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return resultBean;
    }

    public static ResultBean get(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        if (header == null) throw new Exception("请求头不能为空");
        header.put("charset", "utf-8");
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        ResultBean resultBean = new ResultBean();
        //请求参数拼接
        if (null != params && params.size() > 0) {
            StringBuffer param = new StringBuffer();
            param.append("?");
            params.keySet().forEach(key -> {
                param.append(key);
                param.append("=");
                param.append(params.get(key));
                param.append("&");
            });
            param.deleteCharAt(param.length() - 1);
            url = url + param.toString();
        }
        HttpGet httpGet = new HttpGet(url);

        httpGet.setConfig(requestConfig);
        // 请求头设置
        if (null != header && header.size() > 0) {
            header.keySet().forEach(key -> {
                httpGet.setHeader(key, header.get(key));
            });
        }
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(httpGet);
            resultBean.setStatusLine(response.getStatusLine());
            resultBean.setHeaders(Arrays.toString(response.getAllHeaders()));
            HttpEntity httpEntity = response.getEntity();
            resultBean.setHttpEntity(EntityUtils.toString(httpEntity));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return resultBean;
    }
}
