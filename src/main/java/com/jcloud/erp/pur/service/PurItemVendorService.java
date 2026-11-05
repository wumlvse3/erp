package com.jcloud.erp.pur.service;

import com.jcloud.erp.pur.vo.UserVo;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurItemVendorService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PurItemVendorService.class);
    @Autowired private TransactionalService transactionalService;
    public R addObj(String pur_org) {
        try {
            transactionalService.doAddObj(pur_org);
            return R.ok("自动生成成功");
        } catch (Exception e) {
            return R.error("自动生成失败！ " );
        }
    }
}
