package com.jcloud.pur.service;

import com.jcloud.pur.vo.UserVo;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.task.mapper.TaskInfoMapper;
import com.jcloud.system.task.service.TaskExecutionDetailsService;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AdduserService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AdduserService.class);
    @Resource
    private SqlService sqlService;


    @Transactional
    public R addObj(List<UserVo> params) {
        List<String> insertsql = new ArrayList<>();
        for (UserVo uservo : params) {
            UserEntity user = ShiroUtils.getUserEntity();
            String selectsql="select org_code from sys_org  t where FIND_IN_SET(org_id,'"+uservo.getOrg_power()+"') > 0 and inv_flag='Y' and not exists(select 1 from pur_audited s where s.apply_org=t.org_code and user_id='"+uservo.getUser_id()+"' and pur_org='"+uservo.getPur_org().toString()+"')";
            List<Map<String, Object>> Result = sqlService.getAll(selectsql);

            // 遍历 Result 集合，为每个组织生成一条 insert 语句
            for (Map<String, Object> orgMap : Result) {
                String applyOrg = orgMap.get("org_code") != null ? orgMap.get("org_code").toString() : "";
                if (!applyOrg.isEmpty()) {
                    insertsql.add("insert into pur_audited (create_user_id,create_time,pur_org,dept_id,user_id,user_code,user_man,apply_org,apply_dept_id,bill_price,bill_apply,bill_plan,bill_order,dept_name,apply_dept_name) select '"+user.getUserId().toString() +"',NOW(),'"+uservo.getPur_org().toString()+"',IF(t.dept_id = '', NULL, t.dept_id),t.user_id,t.username,t.name,'"+applyOrg+"',IF(t.dept_id = '', NULL, t.dept_id),'N','N','N','N',t2.name,t2.name from sys_user t left join sys_dept t2 on t.dept_id=t2.dept_id where user_id="+
                                          "'" + uservo.getUser_id() + "'");
                }
            }
        }
        if(insertsql.size()==0||insertsql==null){
            return R.error("没有满足条件的新增数据！");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("添加成功");
        }else{
            return R.error("添加失败");
        }

    }


}
