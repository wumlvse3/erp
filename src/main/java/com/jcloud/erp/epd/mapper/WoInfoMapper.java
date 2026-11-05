package com.jcloud.erp.epd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.erp.epd.entity.WoInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作令优先级表 Mapper
 */
@Mapper
public interface WoInfoMapper extends BaseMapper<WoInfo> {

    /**
     * 根据公司号、工作令号查询
     */
    WoInfo selectByWorkNo(@Param("companyCode") String companyCode,
                          @Param("workNo") String workNo);

    /**
     * 查询参与排序（VALID_FLAG='Y'）的工作令，按 优先级日期、优先等级、工作令号 排序
     */
    List<WoInfo> selectValidList(@Param("companyCode") String companyCode);

    /**
     * 查询公司下所有工作令，按优先级序号排序
     */
    List<WoInfo> selectAllList(@Param("companyCode") String companyCode);

    /**
     * 新增工作令
     */
    int insertWo(WoInfo woInfo);

    /**
     * 修改工作令
     */
    int updateWo(WoInfo woInfo);

    /**
     * 批量更新优先级序号
     */
    int batchUpdatePriorityNo(@Param("companyCode") String companyCode,
                              @Param("list") List<WoInfo> list);
}
