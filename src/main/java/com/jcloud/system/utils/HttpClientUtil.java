package com.jcloud.system.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/*
 * ����HttpClient����post����Ĺ�����
 */
public class HttpClientUtil {
 
    public static String doGet(String url) throws Exception {
        //����
        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        //������ص�https����ʽ
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        //�������󼴿�
        org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
        GetMethod httpget = new GetMethod(url);
        System.out.println("======url:" + url);
        try {
            httpclient.executeMethod(httpget);
            return httpget.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        } finally {
            httpget.releaseConnection();
        }
    }

    public static String doPost(String url, RequestEntity requestEntity) throws Exception {
        //����
        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        //������ص�https����ʽ
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        //�������󼴿�
        org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestEntity(requestEntity);
        System.out.println("======url:" + url);
        try {
            httpclient.executeMethod(postMethod);
            return postMethod.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        } finally {
            postMethod.releaseConnection();
        }
    }


    public static void main(String[] args) throws Exception {
        //insertMd�����·�����������ݲ�Ʒ���ɱ�׼���ṩ�ĵ�ַ
        String url = "https://bipapuat.yanjing.com.cn/iuap-ipaas-mdm/iuapmdm/cxf/mdmrs/newcenter/newCenterService/insertMd";
        //��Ա�����ݵ������ݱ���
        String gdCode = "person_info";
        //�ܹ�������Ա�����ݵ�Զ��ϵͳ����
        String systemCode = "system01";

        HttpPost post = new HttpPost(url);
        //HttpPost������
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(10000)
                .build();
        //genInsertVO����������insertMd�Ľ���json�ṹ������
        StringEntity requestEntity = new StringEntity(genInsertVO(gdCode, systemCode).toString(), "utf-8");
        requestEntity.setContentType(ContentType.APPLICATION_JSON.toString());
        post.setConfig(requestConfig);
        post.setEntity(requestEntity);
        //HttpPost��header����Ҫ���ݵ���������

        //tenantid�ֶ��ǵ�ǰϵͳ���⻧ID����Ϊר���������������ϵͳ��ֵΪtenant����ʹ�õ��ǹ����Ƶ�������ϵͳ�ͣ�ֵΪ��Ʒ�ļ��ɱ�׼��tenantid�ı�ע�е�ֵ
        post.setHeader("tenantid", "tenant");
        //mdmtoken��Զ��ϵͳ��token��Ҳ����system01���Զ��ϵͳ�������������õ�token
        post.setHeader("mdmtoken", "eb2e7631-1b5a-4c17-b8c5-7fa5eddff37f");

        String s = doPost(url, new StringRequestEntity(requestEntity.toString()));

//        String resp = EntityUtils.toString(entity, "utf-8");
        System.out.println(s);
    }

    /**
     * ����һ�������ݵ�json����
     *
     * @return
     */
    private static JSONObject getData() {
        JSONObject data = new JSONObject();
        //���������  id��ҵ��id���ǽӿڵ��ñ��봫�ݵĲ�����Ϊ���������ݵ�Ψһ��־�����������Ҫ�޸Ļ��߲�ѯ������ͨ�����id
        data.put("id", "id" + new Random().nextInt(1000000000));
        data.put("name", "����");
        //���պ����������÷�����person_org������ݲ�����һ����֯�������ݣ����ǿ������ò��յĲ��ҷ�ʽ��
        //person_org#name,��ʾͨ����֯��name��������֯��������Ҫ���������֯�����ƣ����Ѽ��ţ�������������Ѽ��������֯��code���ͻᱨ��
        //��������person_org#code,��ʾͨ��code��������֯
        //�������Ϊperson_org��û��ָ�����յĲ��ҷ�ʽ������봫��������գ���֯����mdmCode
        data.put("person_org#name", "���Ѽ���");
        data.put("code", "zhangsan");
        //dr�����־�������1������ɾ�����ݡ�Ĭ�ϲ���͸������ݵ�ʱ�򣬲��ô�
        //data.put("dr", "1");
        //mdm_datastatus������ԣ��������ͽ�����ݣ�Ĭ�ϲ��ô���"3"�ǽ��  "4"�Ƿ�棬ֻ������������ֵ��
        // һ�������Ҫ����������⣬Ҳ���ô�
        //   data.put("mdm_datastatus", "4");

        //�����ӱ�sub_��ǰ׺�������ӱ��code������json���������Ա��Ϣ�и�codeΪjob�ĵ��ӱ�����Ҫ����sub_job,���뻹��family���ӱ�ͬ����sub_familyd���ӱ���Ϣ
        data.put("sub_job", "");
        //ͨ��JSONArray���洢�����ӱ������
        JSONArray sub = new JSONArray();
        JSONObject subData = new JSONObject();
        subData.put("id", "id" + new Random().nextInt(1000000000));
        subData.put("name", "����");
        subData.put("code", "lisi");
        subData.put("job_org#name", "���Ѽ���");
        sub.add(subData);
        data.put("sub_job", sub);
        return data;
    }

    private static JSONObject genInsertVO(String gdCode, String systemCode) {
        JSONArray masterData = new JSONArray();
        //ͨ��JSONArray������������������
        for (int i = 0; i < 2; i++) {
            masterData.add(getData());
        }
        //��󹹽�insetMd�ӿ���Ҫ��json���󣬾������Կ��Բ���MdmInsertVO�������
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemCode", systemCode);
        jsonObject.put("gdCode", gdCode);
        jsonObject.put("masterData", masterData);
        return jsonObject;
    }


    public static CloseableHttpClient getNoSSLClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        // 创建跳过主机名验证的SSL连接套接字工厂
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
        );

        return HttpClientBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

    }
 
}