package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcloud.customer.WmsBaseData.entity.BaseSysCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.vo.BaseSysCodeVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-01-11
 */
public interface BaseSysCodeMapper extends BaseMapper<BaseSysCode> {

    @Select(value = "select * from base_sys_code where parent_id=#{parentId} and org_id=#{orgId}")
    List<BaseSysCodeVo> selectVoList(String parentId, String orgId);
}
