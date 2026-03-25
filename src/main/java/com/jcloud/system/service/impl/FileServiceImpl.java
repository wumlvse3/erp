package com.jcloud.system.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.system.dao.FileDao;
import com.jcloud.system.entity.FileEntity;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.minio.MinioUtil;
import com.jcloud.system.service.FileService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.FileType;
import com.jcloud.system.utils.FileUtil;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;


/**
 * @Auther: yang
 * @Date: 2023/04/04/16:03
 * @Description:
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileDao, FileEntity> implements FileService {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private MinioUtil minioUtil;

    private String files = "/files";



    @Override
    public Boolean getUrlFile(HttpServletResponse response, String filePath, String suffix) {
        try {
            response.setContentType(FileType.MimeType(suffix)); // 不同文件的MimeType参考后续链接
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1"));
            FileCopyUtils.copy(new FileInputStream(filePath), response.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean getFile(HttpServletResponse response, String fileId) {
        try {
            FileEntity fileEntity = getById(fileId);
            String filename = ObjectUtils.oGetS(fileEntity.getFilename());
            String filePath = sqlService.getUploadPath() + fileEntity.getUrl();
            String suffix = filename.substring(filename.lastIndexOf("."));
            response.setContentType(FileType.MimeType(suffix)); // 不同文件的MimeType参考后续链接
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1"));
            FileCopyUtils.copy(new FileInputStream(filePath), response.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean getImg(HttpServletResponse response, String fileId) {
        try {
            FileEntity fileEntity = getById(fileId);
            String filePath = sqlService.getUploadPath() + fileEntity.getUrl();
            response.setContentType("image/jpeg");
            OutputStream toClient = response.getOutputStream();
            String xmlImg = GetImageStr(filePath);
            GenerateImage(xmlImg, toClient);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean GenerateImage(String imgStr, OutputStream out) {
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String GetImageStr(String imgFilePath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回字符串
    }

    /**
     * 文件删除
     *
     * @param fileId 文件id
     * @return
     */
    @Override
    public Boolean deleteFile(Long fileId) {
        String fileName = sqlService.getUploadPath() + getById(fileId).getUrl().replace("/files/", "");
        removeById(fileId);
        return FileUtil.deleteFile(fileName);
    }

    /**
     * 删除文件的本体
     *
     * @param fileId 文件id
     * @return
     */
    public Boolean deleteFile2(Long fileId) {
        String fileName = sqlService.getUploadPath() + getById(fileId).getUrl().replace("/files/", "");
        return FileUtil.deleteFile(fileName);
    }


    /**
     * 上传文件
     *
     * @param typeCode 文件标识编号（在没有地址的时，以编号作为文件地址）
     * @param file     文件
     * @param params   参数
     * @return
     */
    @Override
    @Transactional
    public R uploadFile(String typeCode, MultipartFile file, Map<String, Object> params) {
        String type = ObjectUtils.oGetS(params.get("type")); //文件状态
        String filePath = ObjectUtils.oGetS(params.get("filePath")); //文件地址
        String uploadtype = ObjectUtils.oGetS(params.get("uploadtype")); //状态 0=不覆盖文件 1=覆盖文件
        String fileId = ObjectUtils.oGetS(params.get("fileId")); //文件id
        String fileName = file.getOriginalFilename();
        String fileName2 = IdWorker.getIdStr();
        if (filePath.equals("")) filePath = "/" + DateUtil.today() + "/" + typeCode + "/";
        if (uploadtype.equals("1") && !fileId.equals("")) {//覆盖文件
//            QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("id",typeId);
            FileEntity fileEntity = getById(fileId);
//            List<FileEntity> list = list(queryWrapper);
            if (fileEntity != null) {
                deleteFile2(fileEntity.getId());
                if (type == null || type == "") fileEntity.setType(FileType.fileType(fileName));
                else fileEntity.setType(Integer.valueOf(type));
                fileEntity.setFilename(fileName);
                fileEntity.setUrl(filePath + fileName2);
                fileEntity.setCreateDate(new Date());
                UserEntity userEntity = ShiroUtils.getUserEntity();
                if (userEntity == null) {
                    fileEntity.setCreateBy("0");
                } else {
                    fileEntity.setCreateBy(userEntity.getUsername());
                }
                try {
                    minioUtil.upload(file, files + filePath + fileName2);
//                    FileUtil.uploadFile(file.getBytes(), sqlService.getUploadPath() + filePath, fileName2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return R.error(e.getCause().toString());
                }
                if (updateById(fileEntity)) return R.ok("上传成功").setdata(fileEntity);
                else return R.error("上传失败");
            }
        }
        FileEntity fileEntity = new FileEntity();
        if (type == null || type == "") fileEntity.setType(FileType.fileType(fileName));
        else fileEntity.setType(Integer.valueOf(type));
        fileEntity.setFilename(fileName);
        fileEntity.setUrl(filePath + fileName2);
        fileEntity.setCreateDate(new Date());
        UserEntity userEntity = ShiroUtils.getUserEntity();
        if (userEntity == null) {
            fileEntity.setCreateBy("0");
        } else {
            fileEntity.setCreateBy(userEntity.getUsername());
        }
        fileEntity.setTypeCode(typeCode);
        try {
            minioUtil.upload(file, files + filePath + fileName2);
//            FileUtil.uploadFile(file.getBytes(), sqlService.getUploadPath() + filePath, fileName2);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getCause().toString());
        }
        if (save(fileEntity)) return R.ok("上传成功").setdata(fileEntity);
        else return R.error("上传失败");
    }
}
