package com.jcloud.customer.WmsBaseData.service.Impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.customer.WmsBaseData.entity.ScoringGroup;
import com.jcloud.customer.WmsBaseData.entity.ScoringModelDetail;
import com.jcloud.customer.WmsBaseData.mapper.ScoringGroupMapper;
import com.jcloud.customer.WmsBaseData.mapper.ScoringModelDetailMapper;
import com.jcloud.customer.WmsBaseData.service.ScoringGroupService;
import com.jcloud.customer.WmsBaseData.service.ScoringModelDetailService;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.utils.MessageUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-06-16
 */
@Service
public class ScoringGroupServiceImpl extends ServiceImpl<ScoringGroupMapper, ScoringGroup> implements ScoringGroupService {

    @Autowired
    private ComUnitService comUnitService;

    @Transactional
    public R save(Map<String, Object> params) {
        try {
            R res = comUnitService.SaveData(params);
            boolean multi = comUnitService.checkMulti("scoring_group", "scoring_group_code");
            if (!multi) {
                throw new Exception(MessageUtils.get("E37", ""));
            }

            String listdatas = ObjectUtils.oGetS(params.get("listdatas"));//列表数据
            List<List<Map<String, Object>>> list = JSONUtil.parseArray(listdatas).toBean(List.class);
            if (list.get(0).size() == 0) {
                throw new Exception(MessageUtils.get("E61", ""));
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(e.getMessage());
        }
    }
}
