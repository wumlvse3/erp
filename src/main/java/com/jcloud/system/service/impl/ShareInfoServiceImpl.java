package com.jcloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.system.mapper.ShareInfoMapper;
import com.jcloud.system.entity.ShareInfo;
import com.jcloud.system.service.ShareInfoService;
import com.jcloud.system.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (ShareInfo)表服务实现类
 *
 * @author Bianxs
 * @since 2023-09-12 14:03:01
 */
@Service("shareInfoService")
public class ShareInfoServiceImpl extends ServiceImpl<ShareInfoMapper, ShareInfo> implements ShareInfoService {
    @Override
    public List<ShareInfo> getPublicShareInfos() {
        List<ShareInfo> dataList = baseMapper.selectList(new QueryWrapper<ShareInfo>().eq("receiver_range","EXTERNAL"));
        return dataList;
    }
}

