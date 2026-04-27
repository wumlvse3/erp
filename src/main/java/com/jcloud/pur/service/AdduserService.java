package com.jcloud.pur.service;

import com.jcloud.pur.vo.UserVo;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.task.mapper.TaskInfoMapper;
import com.jcloud.system.task.service.TaskExecutionDetailsService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class AdduserService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdduserService.class);
    @Resource
    private SqlService sqlService;


    @Transactional
    public R addObj(UserVo uservo) {
        List<String> insertsql = Collections.emptyList();
        insertsql.add("insert into pur_audited (pur_org,dept,user,username,apply_org,apply_dept,bill_price,bill_apply,bill_plan,bill_order) select org_power,dept_id,user_id,name,org_power,'N','N','N','N' from sys_user where user_id="+
                              "'" + uservo.getUser_id() + "'");
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("添加成功");
        }else{
            return R.error("添加失败");
        }

    }


}
