package com.jcloud.erp.epd.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcloud.erp.epd.entity.WoInfo;
import com.jcloud.erp.epd.mapper.WoInfoMapper;
import com.jcloud.erp.epd.service.WoInfoService;
import com.jcloud.system.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作令优先级 Service 实现
 *
 * 排序规则：优先级日期越小序号越小；优先级日期相同时，优先等级越小序号越小。
 * 序号按 10 的倍数保存：10、20、30 ...
 */
@Service
public class WoInfoServiceImpl extends ServiceImpl<WoInfoMapper, WoInfo> implements WoInfoService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 序号步长 */
    private static final int SEQ_STEP = 10;

    /** 批量更新序号的分批大小 */
    private static final int BATCH_SIZE = 500;

    @Autowired
    private WoInfoMapper woInfoMapper;

    @Override
    @Transactional
    public R addWo(WoInfo woInfo) {
        try {
            R check = checkRequired(woInfo);
            if (check != null) {
                return check;
            }

            WoInfo exist = woInfoMapper.selectByWorkNo(woInfo.getCompany_code(), woInfo.getWork_no());
            if (exist != null) {
                return R.error("工作令号【" + woInfo.getWork_no() + "】已存在");
            }

            if (StringUtils.isEmpty(woInfo.getValid_flag())) {
                woInfo.setValid_flag("Y");
            }
            // 序号由重排逻辑统一分配
            woInfo.setPriority_no(null);

            woInfoMapper.insertWo(woInfo);

            // 重算所有参与排序工作令的优先级序号
            List<Map<String, Object>> historyList = renumberPriorityNo(woInfo.getCompany_code());
            savePriorityHistory("ADD", woInfo, historyList);

            WoInfo added = woInfoMapper.selectByWorkNo(woInfo.getCompany_code(), woInfo.getWork_no());
            return R.ok("新增成功").setdata(added);
        } catch (Exception e) {
            logger.error("新增工作令失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("新增工作令失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R updateWo(WoInfo woInfo) {
        try {
            R check = checkRequired(woInfo);
            if (check != null) {
                return check;
            }

            WoInfo exist = woInfoMapper.selectByWorkNo(woInfo.getCompany_code(), woInfo.getWork_no());
            if (exist == null) {
                return R.error("工作令号【" + woInfo.getWork_no() + "】不存在");
            }

            if (StringUtils.isEmpty(woInfo.getValid_flag())) {
                woInfo.setValid_flag(exist.getValid_flag());
            }
            if (StringUtils.isEmpty(woInfo.getWork_desc())) {
                woInfo.setWork_desc(exist.getWork_desc());
            }
            if (StringUtils.isEmpty(woInfo.getPick_status())) {
                woInfo.setPick_status(exist.getPick_status());
            }

            woInfoMapper.updateWo(woInfo);

            // 重算所有参与排序工作令的优先级序号
            List<Map<String, Object>> historyList = renumberPriorityNo(woInfo.getCompany_code());
            savePriorityHistory("UPDATE", woInfo, historyList);

            WoInfo updated = woInfoMapper.selectByWorkNo(woInfo.getCompany_code(), woInfo.getWork_no());
            return R.ok("修改成功").setdata(updated);
        } catch (Exception e) {
            logger.error("修改工作令失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("修改工作令失败：" + e.getMessage());
        }
    }

    @Override
    public R queryList(String companyCode) {
        try {
            if (StringUtils.isEmpty(companyCode)) {
                return R.error("公司号不能为空");
            }
            List<WoInfo> list = woInfoMapper.selectAllList(companyCode);
            return R.ok().setdata(list);
        } catch (Exception e) {
            logger.error("查询工作令列表失败", e);
            return R.error("查询工作令列表失败：" + e.getMessage());
        }
    }

    /**
     * 必填校验
     */
    private R checkRequired(WoInfo woInfo) {
        if (woInfo == null) {
            return R.error("参数不能为空");
        }
        if (StringUtils.isEmpty(woInfo.getCompany_code())) {
            return R.error("公司号不能为空");
        }
        if (StringUtils.isEmpty(woInfo.getWork_no())) {
            return R.error("工作令号不能为空");
        }
        if (StringUtils.isEmpty(woInfo.getPriority_date())) {
            return R.error("优先级日期不能为空");
        }
        if (woInfo.getPriority_level() == null) {
            return R.error("优先等级不能为空");
        }
        return null;
    }

    /**
     * 重算参与排序工作令的优先级序号。
     * 按 优先级日期、优先等级、工作令号 排序后，依次分配 10、20、30 ...
     *
     * @return 序号发生变化的记录（workNo、oldNo、newNo），用于保存历史记录
     */
    private List<Map<String, Object>> renumberPriorityNo(String companyCode) {
        List<WoInfo> validList = woInfoMapper.selectValidList(companyCode);
        List<WoInfo> updateList = new ArrayList<>();
        List<Map<String, Object>> historyList = new ArrayList<>();

        int seq = SEQ_STEP;
        for (WoInfo wo : validList) {
            Integer oldNo = wo.getPriority_no();
            if (oldNo == null || oldNo.intValue() != seq) {
                Map<String, Object> his = new HashMap<>();
                his.put("workNo", wo.getWork_no());
                his.put("oldNo", oldNo);
                his.put("newNo", seq);
                historyList.add(his);

                wo.setPriority_no(seq);
                updateList.add(wo);
            }
            seq += SEQ_STEP;
        }

        // 分批更新
        for (int i = 0; i < updateList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, updateList.size());
            woInfoMapper.batchUpdatePriorityNo(companyCode, updateList.subList(i, end));
        }
        return historyList;
    }

    /**
     * 保存优先级调整历史记录。
     * TODO 历史表建表语句确认后，在此处将 historyList 写入历史表。
     *
     * @param operType    操作类型：ADD-新增，UPDATE-修改
     * @param woInfo      本次操作的工作令
     * @param historyList 序号变化记录：workNo、oldNo、newNo
     */
    private void savePriorityHistory(String operType, WoInfo woInfo, List<Map<String, Object>> historyList) {
        if (historyList == null || historyList.isEmpty()) {
            return;
        }
        logger.info("工作令优先级调整历史，操作类型：{}，公司号：{}，工作令号：{}，序号变化：{}",
                operType, woInfo.getCompany_code(), woInfo.getWork_no(), historyList);
    }
}
