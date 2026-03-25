package com.jcloud.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.system.mapper.SysFileMapper;
import com.jcloud.system.entity.SysFile;
import com.jcloud.system.service.SysFileService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (SysFile)表服务实现类
 *
 * @author Bianxs
 * @since 2024-02-19 10:36:37
 */
@Service("sysFileService")
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    @Value("${jcloud.uploadPath}")
    private String rootPath;//密码
    @Override
    @Transactional
    public R generalFileRecord(String path) {
        //获取指定目录下的所有文件或者目录的File数组
        List<Map> fileList= new ArrayList<>();
        File srcFile = new File(rootPath+File.separator+path);
        getAllFilePath(srcFile,path,fileList);
        Map<String,List<Map>> storeList = fileList.stream().collect(Collectors.groupingBy(a ->String.valueOf(a.get("keyWord"))));
        JSONArray result = new JSONArray();
        for (String key : storeList.keySet()) {
            System.out.println(key + ":" + storeList.get(key));
            JSONObject obj = new JSONObject();
            obj.put("keyWord",key);
            obj.put("files", JSON.toJSONString(storeList.get(key)));
            result.add(obj);
        }
        return R.ok().setdata(result);
    }
    private void getAllFilePath(File srcFile,String keyPath,List fileList){
        File[] fileArray = srcFile.listFiles();
        //遍历该File数组，得到每一个File对象
        if(fileArray != null){
            for (File file :fileArray){
                //判断file对象是否为目录
                if (file.isDirectory()){
                    //是：递归调用
                    getAllFilePath(file,keyPath,fileList);
                }else{
                    //否：获取绝对路径输出在控制台
                    String absolutePath = file.getAbsolutePath();
                    absolutePath = absolutePath.replace("\\","/");
                    absolutePath = absolutePath.substring(absolutePath.indexOf(keyPath),absolutePath.length());
                    SysFile bean = new SysFile();
                    bean.setFilename(file.getName());
                    bean.setUrl(absolutePath);
                    bean.setType(5);
                    baseMapper.insert(bean);
                    String[] bFile = absolutePath.split("/");
                    String keyWord = bFile.length>1?bFile[1]:"";
                    Map obj = new HashMap();
                    obj.put("uid",bean.getId());
                    obj.put("name",bean.getFilename());
                    obj.put("url","/sys/file/getfile/"+bean.getId()+"?date=1");
                    obj.put("keyWord",keyWord);
                    obj.put("id",bean.getId());
                    obj.put("status","success");
                    fileList.add(obj);
                }
            }
        }
    }
}

