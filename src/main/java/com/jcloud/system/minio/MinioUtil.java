package com.jcloud.system.minio;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.minio.*;
import io.minio.http.Method;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {
    

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig configuration;

    private String files = "/files";


    /**
     * 上传文件
     *
     * @param file     文件
     * @param fileName 文件名称
     */
    public void upload(MultipartFile file, String fileName) {
        // 使用putObject上传一个文件到存储桶中。
        try {
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(configuration.getBucketName())
                    .object(files+fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     */
    public InputStream getInput( String fileName) {
        InputStream in = null;
        try {
            // 文件下载
            in = minioClient.getObject(GetObjectArgs.builder().bucket(configuration.getBucketName()).object(files+fileName).build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return  in;
        }
    }


    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    public void delete(String fileName) {
        try {
             minioClient.removeObject(RemoveObjectArgs.builder().bucket(configuration.getBucketName()).object(files+fileName).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     *
     * @param fileName 文件名称
     */
    public File getFile( String fileName) {
        File file = null;
        try {
            file = File.createTempFile(IdWorker.getIdStr(), "");//创建临时文件
            // 文件下载
            InputStream input = minioClient.getObject(GetObjectArgs.builder().bucket(configuration.getBucketName()).object(files+fileName).build());
            try (FileOutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024]; // 创建缓冲区
                int bytesRead;
                // 读取 InputStream 并写入到文件
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return  file;
        }
    }

}