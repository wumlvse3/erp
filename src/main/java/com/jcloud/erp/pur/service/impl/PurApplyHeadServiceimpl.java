package com.jcloud.erp.pur.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.pur.entity.OrdOrderHead;
import com.jcloud.erp.pur.entity.PurApplyHead;
import com.jcloud.erp.pur.mapper.PurApplyHeadMapper;
import com.jcloud.erp.pur.service.PurApplyHeadService;
import com.jcloud.erp.pur.service.TransactionalService;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
public class PurApplyHeadServiceimpl extends ServiceImpl<PurApplyHeadMapper, PurApplyHead> implements PurApplyHeadService {
    @Resource
    private SqlService sqlService;

    @Autowired
    private TransactionalService transactionalService;

    @Override
    public R addObj(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_head set bill_status = '已提交' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_detail set line_status = '已提交' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("提交成功");
        }else{
            return R.error("提交失败");
        }
    }

    @Override
    public R unaddObj(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_head set bill_status = '未提交' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_detail set line_status = '未提交' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消提交成功");
        }else{
            return R.error("取消提交失败");
        }
    }

    @Override
    public R auditlist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();

        for (PurApplyHead purapplyhead : params) {
            String approver_opinion=purapplyhead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                purapplyhead.setApprover_opinion("");
            }
            insertsql.add("update pur_apply_head set bill_status = '审核通过',approver_opinion='"+purapplyhead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + purapplyhead.getUu_id() + "' and (bill_status='已提交' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_apply_head.pur_org and apply_check='Y' ))  ");
            insertsql.add("update pur_apply_detail set line_status = '审核通过' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "' ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("审核成功");
        }else{
            return R.error("审核失败");
        }
    }

    @Override
    public R passlist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurApplyHead purapplyhead : params) {
            String approver_opinion=purapplyhead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                purapplyhead.setApprover_opinion("");
            }
            insertsql.add("update pur_apply_head set bill_status = '已提交' where uu_id = '" + purapplyhead.getUu_id() + "' and   (bill_status='审核通过' or not exists(select 1 from PUR_PARAMETER where PUR_PARAMETER.pur_org=pur_apply_head.pur_org and apply_check='Y' )) ");
            insertsql.add("update pur_apply_detail set line_status = '已提交' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "'  ");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("取消审核成功");
        }else{
            return R.error("取消审核失败");
        }
    }

    @Override
    public R bhpasslist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurApplyHead purapplyhead : params) {
            String approver_opinion=purapplyhead.getApprover_opinion();
            if(approver_opinion==null||"null".equals(approver_opinion)){
                purapplyhead.setApprover_opinion("");
            }
            insertsql.add("update pur_apply_head set bill_status = '驳回',approver_opinion='"+purapplyhead.getApprover_opinion()+"',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where uu_id = '" + purapplyhead.getUu_id() + "'  and bill_status='审核通过'  ");
            insertsql.add("update pur_apply_detail set line_status = '驳回' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "' ");
          }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("驳回成功");
        }else{
            return R.error("驳回失败");
        }
    }

    @Override
    public R auditdetaillist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurApplyHead purapplyhead : params) {
            String line_opinion=purapplyhead.getLine_opinion();
            if(line_opinion==null||"null".equals(line_opinion)){
                purapplyhead.setLine_opinion("");
            }
            insertsql.add("update pur_apply_detail set line_status = '审核通过',line_opinion='"+purapplyhead.getLine_opinion()+"' where uu_id = '" + purapplyhead.getUu_id() + "' ");
            insertsql.add("update pur_apply_head set bill_status = '审核通过',approver_user_man='"+user.getName()+"',approver_user_id='"+user.getUserId()+"',approver_user_code='"+user.getUsername()+"',approver_time=NOW() where not exists (select 1 from pur_apply_detail where uk_apply_uuid = '" + purapplyhead.getUk_apply_uuid() + "' and line_status<>'审核通过' and pur_apply_detail.uk_apply_uuid=pur_apply_head.uu_id)");

        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("审核成功");
        }else{
            return R.error("审核失败");
        }
    }

    @Override
    public R passdetaillist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        UserEntity user = ShiroUtils.getUserEntity();
        for (PurApplyHead purapplyhead : params) {
            String line_opinion=purapplyhead.getLine_opinion();
            if(line_opinion==null||"null".equals(line_opinion)){
                purapplyhead.setLine_opinion("");
            }
            insertsql.add("update pur_apply_detail set line_status = '驳回',line_opinion='' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_head set bill_status = '驳回',approver_user_man='',approver_user_id=null,approver_user_code='',approver_time=null,approver_opinion=null where uu_id='" + purapplyhead.getUk_apply_uuid() + "' and  exists (select 1 from pur_apply_detail where uk_apply_uuid = '" +purapplyhead.getUk_apply_uuid() +"' and line_status='驳回' and pur_apply_detail.uk_apply_uuid=pur_apply_head.uu_id)");
         }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("驳回成功");
        }else{
            return R.error("驳回失败");
        }
    }

    @Override
    public R detaillist(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update ord_order_detail set is_apply='0'  where  exists(select 1 from pur_apply_detail where pur_apply_detail.uu_id = '" + purapplyhead.getUu_id() +"' and pur_apply_detail.up_bill=ord_order_detail.uk_order_no and pur_apply_detail.up_bill_line=ord_order_detail.uk_line_no )");
            insertsql.add("delete from pur_apply_detail  where uu_id = '" + purapplyhead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @Override
    public R enable(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_head set is_active = 'Y' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_detail set is_active = 'Y' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "' and apply_qty>sum_plan");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R disabl(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_head set is_active = 'N' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_detail set is_active = 'N' where uk_apply_uuid = '" + purapplyhead.getUu_id() + "'");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }

    @Override
    public R detailenable(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_detail set is_active = 'Y' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_head set is_active = 'Y' where uu_id = '" + purapplyhead.getUk_apply_uuid() + "' and  exists( select 1 from pur_apply_detail where is_active = 'Y' and uk_apply_uuid = '" + purapplyhead.getUk_apply_uuid() + "' and pur_apply_detail.uk_apply_uuid=pur_apply_head.uu_id)");
        }

        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("启用成功");
        }else{
            return R.error("启用失败");
        }
    }

    @Override
    public R detaildisabl(List<PurApplyHead> params) {
        List<String> insertsql = new ArrayList<>();
        for (PurApplyHead purapplyhead : params) {
            insertsql.add("update pur_apply_detail set is_active = 'N' where uu_id = '" + purapplyhead.getUu_id() + "'");
            insertsql.add("update pur_apply_head set is_active = 'N' where uu_id = '" + purapplyhead.getUk_apply_uuid() + "' and not exists( select 1 from pur_apply_detail where is_active = 'Y' and uk_apply_uuid = '" + purapplyhead.getUk_apply_uuid() + "' and pur_apply_detail.uk_apply_uuid=pur_apply_head.uu_id)");
        }
        boolean flag = sqlService.insertList(insertsql);
        if (flag){
            return R.ok("关闭成功");
        }else{
            return R.error("关闭失败");
        }
    }


}
