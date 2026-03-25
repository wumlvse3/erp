package com.jcloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.system.entity.FileEntity;
import com.jcloud.system.utils.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 文件操作逻辑层
 * @Auther: yang
 * @Date: 2023/04/04/15:16
 * @Description:
 */
public interface FileService extends IService<FileEntity> {

    /**
     * 通过地址获取文件
     * @param response
     * @param filePath 文件地址
     * @param suffix   文件类型
     * @return
     */
    Boolean getUrlFile( HttpServletResponse response, String filePath,String suffix);

    /**
     * 通过id返回文件
     * @param response
     * @param fileId  文件id
     * @return
     */
    Boolean getFile(HttpServletResponse response, String fileId) ;

    /**
     * 通过id返回图片(图片流方便浏览器访问)
     * @param response
     * @param fileId  文件id
     * @return
     */
    Boolean getImg(HttpServletResponse response, String fileId) ;

    /**
     * 删除文件
     * @param fileId
     * @return
     */
    Boolean deleteFile(Long fileId);

    /**
     * 上传文件
     * @param typeCode 文件标识编号（在没有地址的时，以编号作为文件地址）
     * @param file     文件
     * @param params   参数
     * @return
     */
    R uploadFile(String typeCode,  MultipartFile file, Map<String, Object> params);

}
