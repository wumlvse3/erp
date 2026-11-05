package com.jcloud.pur.service;
import com.jcloud.pur.entity.OrdOrderHead;
import com.jcloud.pur.entity.PurApplyHead;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionalService {
    @Autowired
    private SqlService sqlService;
    @Transactional(rollbackFor = Exception.class)
    public void doAddObj(String pur_org) {
        UserEntity user = ShiroUtils.getUserEntity();
        sqlService.deleteOne("delete from pur_item_vendor_head t1 where t1.pur_org='" + pur_org + "' and exists(select 1 from pur_agreement_head t2,pur_agreement_detail t3 where t2.pur_org=t1.pur_org and t2.uu_id=t3.uk_agreement_uuid and t1.item_code=t3.item_code and t2.bill_status='审核通过' and effective_date<=CURDATE() and expiring_date>=CURDATE())");
        sqlService.deleteOne("delete from pur_item_vendor t1 where t1.pur_org='" + pur_org + "' and exists(select 1 from pur_agreement_head t2,pur_agreement_detail t3 where t2.pur_org=t1.pur_org and t2.uu_id=t3.uk_agreement_uuid and t1.vendor_code=t2.vendor_code and t1.item_code=t3.item_code and t2.bill_status='审核通过' and effective_date<=CURDATE() and expiring_date>=CURDATE())");
        sqlService.insertOne("insert into pur_item_vendor_head(create_user_id,create_time,pur_org,dept_code,dept_name,item_id,item_code,item_name,item_spec,item_model,unit_id,unit_name) select '" + user.getUserId().toString() + "',NOW(),t2.pur_org,t4.dept_code,t4.dept_name,t3.item_id,t5.item_code,t5.item_name,t5.item_spec,t5.item_model,t5.stock_unit,t6.unit_name from pur_agreement_head t2,pur_agreement_detail t3,pur_group t4,epd_item_master t5,epd_unit_measure t6 where t2.uu_id=t3.uk_agreement_uuid and t2.pur_org=t4.pur_org  and t3.item_code=t5.item_code and t2.pur_org=t5.inv_org and t5.stock_unit=t6.unit_id and t2.role_code=t4.role_code and t2.pur_org='" + pur_org + "'  and t2.bill_status='审核通过' and effective_date<=CURDATE() and expiring_date>=CURDATE()");
        sqlService.insertOne("insert into pur_item_vendor(create_user_id,create_time,pur_org,item_id,item_code,item_name,vendor_id,vendor_code,vendor_name,is_allocation,create_way,auto_flag,min_number) select '" + user.getUserId().toString() + "',NOW(),t2.pur_org,item_id,item_code,item_name,t2.vendor_id,t2.vendor_code,t2.vendor_name,'Y','自动生成','Y',main_qty from pur_agreement_head t2,pur_agreement_detail t3 where t2.pur_org='" + pur_org + "' and t2.uu_id=t3.uk_agreement_uuid  and t2.bill_status='审核通过' and effective_date<=CURDATE() and expiring_date>=CURDATE()");
        sqlService.updateOne("UPDATE pur_item_vendor t1 JOIN (SELECT pur_org, item_code, vendor_code, COUNT(IF(auto_flag = 'Y', 1, NULL)) as cnt_auto_y, (100 - IFNULL(SUM(IF(is_allocation = 'Y', IFNULL(allocation_per, 0), 0)), 0)) as remaining_alloc FROM pur_item_vendor WHERE pur_org = '" + pur_org + "' GROUP BY pur_org, item_code, vendor_code) t2 ON t1.pur_org = t2.pur_org AND t1.item_code = t2.item_code AND t1.vendor_code = t2.vendor_code SET t1.allocation_per = t2.remaining_alloc / NULLIF(t2.cnt_auto_y, 0), t1.auto_flag = 'N' WHERE t1.pur_org = '" + pur_org + "' AND t1.auto_flag = 'Y'");

    }

    public void dopurApply(List<OrdOrderHead> params) {
        UserEntity user = ShiroUtils.getUserEntity();
        String deptId = user.getDeptId();
        List<String> insertsql = new ArrayList<>();
        String sql = "select dept_code, name from sys_dept where dept_id = '" + deptId + "'";
        for (OrdOrderHead purapplyhead : params) {
            String sqls = "SELECT CASE WHEN MAX(uk_apply_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0000') ELSE LPAD(CAST(MAX(uk_apply_no) AS UNSIGNED), 12, '0') END AS uk_apply_no FROM pur_apply_head WHERE LEFT(uk_apply_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
            Map<String, Object> apply_no = sqlService.getOne(sqls);
            String uk_apply_no= apply_no.get("uk_apply_no").toString();
            Map<String, Object> dept = sqlService.getOne(sql);
            sqlService.insertOne("insert into pur_apply_head(create_user_id,create_time,s_uu_id,up_bill,pur_org,apply_dept_id,apply_dept_code,apply_dept_name,apply_date,apply_user_id,apply_user_code,apply_user_man,bill_status,type_code) select '" + user.getUserId().toString() + "',NOW(),uu_id,uk_order_no,'"+purapplyhead.getPur_org()+"','"+ user.getDeptId()+"','"+dept.get("dept_code")+"','"+dept.get("name")+"',CURDATE(),'"+user.getUserId()+"','"+user.getUsername()+"','"+user.getName()+"','未提交',1 from ord_order_head t2 where  t2.bill_status=2 and t2.uu_id='" + purapplyhead.getUu_id() +"' and  not exists(select 1 from pur_apply_head t1 where t2.uu_id=t1.s_uu_id ) ");
            sqlService.insertOne("insert into pur_apply_detail(apply_org,s_uu_id,create_user_id,create_time,uk_apply_uuid,uk_apply_no,up_bill,up_bill_line,item_id,item_code,item_name,item_spec,item_model,apply_qty,end_date,pur_org,pur_unit_id,pur_unit_name,main_qty,main_unit_id,main_unit_name,work_order,project_id,project_no,project_name,line_status)  \n" +
                    "select t1.send_org,t1.uu_id,'" + user.getUserId().toString() + "',NOW(),t2.uu_id,t2.uk_apply_no,t1.uk_order_no,t1.uk_line_no,item_id,item_code,item_name,item_spec,item_model,pur_qty,CURDATE(),t2.pur_org,ord_unit_id,ord_unit_name,main_qty,main_unit_id,main_unit_name,work_order,project_id,project_no,project_name,'未提交' from ord_order_detail t1,pur_apply_head t2 where t1.uk_order_uuid=t2.s_uu_id  and t1.is_apply='0' and t1.is_pur='1'  and t2.s_uu_id='" + purapplyhead.getUu_id() +"'");
            sqlService.updateOne("UPDATE pur_apply_head t JOIN (SELECT uu_id, CONCAT(LEFT('"+uk_apply_no+"', 8), LPAD(ROW_NUMBER() OVER (ORDER BY uu_id ASC)+SUBSTRING('"+uk_apply_no+"', 9), 4, '0')) as new_uk_apply_no FROM pur_apply_head WHERE uk_apply_no IS NULL OR uk_apply_no = '') tmp ON t.uu_id = tmp.uu_id SET t.uk_apply_no = tmp.new_uk_apply_no where s_uu_id='"+purapplyhead.getUu_id() +"'");
            sqlService.updateOne("UPDATE pur_apply_detail t JOIN (SELECT t1.uu_id, t2.uk_apply_no AS new_uk_apply_no, CONVERT(IFNULL(MAX(t1.uk_line_no), 0) + 10, SIGNED) AS new_uk_line_no FROM pur_apply_detail t1 INNER JOIN pur_apply_head t2 ON t1.uk_apply_uuid = t2.uu_id WHERE t1.uk_apply_no IS NULL OR t1.uk_apply_no = '' GROUP BY t1.uu_id, t2.uk_apply_no) tmp ON t.uu_id = tmp.uu_id SET t.uk_apply_no = tmp.new_uk_apply_no, t.uk_line_no = tmp.new_uk_line_no where exists(select 1 from ord_order_detail tv where tv.uk_order_uuid='"+purapplyhead.getUu_id() +"' and tv.uu_id=t.s_uu_id )");
            sqlService.updateOne("UPDATE ord_order_detail set is_apply='1' where  exists(select 1 from pur_apply_detail t1 where t1.s_uu_id=ord_order_detail.uu_id and uk_order_uuid='"+purapplyhead.getUu_id() +"' )");
        }


    }

    public void dodelpurApply(List<OrdOrderHead> params) {
        UserEntity user = ShiroUtils.getUserEntity();
        String deptId = user.getDeptId();
        List<String> insertsql = new ArrayList<>();
        String sql = "select dept_code, name from sys_dept where dept_id = '" + deptId + "'";
        String sqls = "SELECT CASE WHEN MAX(uk_apply_no) IS NULL THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y%m%d'), '0000') ELSE LPAD(CAST(MAX(uk_apply_no) AS UNSIGNED), 12, '0') END AS uk_apply_no FROM pur_apply_head WHERE LEFT(uk_apply_no, 8) = DATE_FORMAT(CURDATE(), '%Y%m%d')";
        Map<String, Object> apply_no = sqlService.getOne(sqls);
        String uk_apply_no= apply_no.get("uk_apply_no").toString();
        Map<String, Object> dept = sqlService.getOne(sql);
        for (OrdOrderHead purapplyhead : params) {
            sqlService.insertOne("insert into pur_apply_head(create_user_id,create_time,s_uu_id,up_bill,pur_org,apply_dept_id,apply_dept_code,apply_dept_name,apply_date,apply_user_id,apply_user_code,apply_user_man,bill_status,type_code) select '" + user.getUserId().toString() + "',NOW(),uu_id,uk_order_no,'"+purapplyhead.getPur_org()+"','"+ user.getDeptId()+"','"+dept.get("dept_code")+"','"+dept.get("name")+"',CURDATE(),'"+user.getUserId()+"','"+user.getUsername()+"','"+user.getName()+"','未提交',1 from ord_order_head t2 where  t2.bill_status=2 and t2.uu_id='" + purapplyhead.getUk_order_uuid() +"'  ");
            sqlService.insertOne("insert into pur_apply_detail(apply_org,s_uu_id,create_user_id,create_time,uk_apply_uuid,uk_apply_no,up_bill,up_bill_line,item_id,item_code,item_name,item_spec,item_model,apply_qty,end_date,pur_org,pur_unit_id,pur_unit_name,main_qty,main_unit_id,main_unit_name,work_order,project_id,project_no,project_name,line_status)  \n" +
                    "select t1.send_org,t1.uu_id,'" + user.getUserId().toString() + "',NOW(),t2.uu_id,t2.uk_apply_no,t1.uk_order_no,t1.uk_line_no,item_id,item_code,item_name,item_spec,item_model,pur_qty,CURDATE(),t2.pur_org,ord_unit_id,ord_unit_name,main_qty,main_unit_id,main_unit_name,work_order,project_id,project_no,project_name,'未提交' from ord_order_detail t1,pur_apply_head t2 where t1.uk_order_uuid=t2.s_uu_id and t2.uk_apply_no is null and t1.is_apply='0' and t1.is_pur='1'  and t1.uu_id='" + purapplyhead.getUu_id() +"'");
            sqlService.updateOne("UPDATE pur_apply_head t JOIN (SELECT uu_id, CONCAT(LEFT('"+uk_apply_no+"', 8), LPAD(ROW_NUMBER() OVER (ORDER BY uu_id ASC)+SUBSTRING('"+uk_apply_no+"', 9), 4, '0')) as new_uk_apply_no FROM pur_apply_head WHERE uk_apply_no IS NULL OR uk_apply_no = '') tmp ON t.uu_id = tmp.uu_id SET t.uk_apply_no = tmp.new_uk_apply_no where s_uu_id='"+purapplyhead.getUk_order_uuid() +"'");
            sqlService.updateOne("UPDATE pur_apply_detail t JOIN (SELECT t1.uu_id, t2.uk_apply_no AS new_uk_apply_no, CONVERT(IFNULL(MAX(t1.uk_line_no), 0) + 10, SIGNED) AS new_uk_line_no FROM pur_apply_detail t1 INNER JOIN pur_apply_head t2 ON t1.uk_apply_uuid = t2.uu_id WHERE t1.uk_apply_no IS NULL OR t1.uk_apply_no = '' GROUP BY t1.uu_id, t2.uk_apply_no) tmp ON t.uu_id = tmp.uu_id SET t.uk_apply_no = tmp.new_uk_apply_no, t.uk_line_no = tmp.new_uk_line_no where exists(select 1 from ord_order_detail tv where tv.uu_id='"+purapplyhead.getUu_id() +"' and tv.uu_id=t.s_uu_id )");
            sqlService.updateOne("UPDATE ord_order_detail set is_apply='1' where  exists(select 1 from pur_apply_detail t1 where t1.s_uu_id=ord_order_detail.uu_id and ord_order_detail.uu_id='"+purapplyhead.getUu_id() +"' )");
        }


    }
}
