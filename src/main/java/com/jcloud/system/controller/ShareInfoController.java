package com.jcloud.system.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcloud.system.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcloud.system.entity.ShareInfo;
import com.jcloud.system.service.ShareInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * (ShareInfo)表控制层
 *
 * @author Bianxs
 * @since 2023-09-12 14:03:00
 */
@RestController
@RequestMapping("shareInfo")
@Api(tags = {""})
public class ShareInfoController {
    /**
     * 服务对象
     */
    @Resource
    private ShareInfoService shareInfoService;


    /**
    * 获取用以外部进行公共下载的数据
    *
     * @param null
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2023/9/12 14:04
    */
    @RequestMapping(value = "/public",method = RequestMethod.GET)
    public List<ShareInfo> getPublicShareInfo()  {

        return shareInfoService.getPublicShareInfos();
    }
}

