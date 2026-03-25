package com.jcloud.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.system.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * @Auther: yang
 * @Date: 2023/03/27/9:26
 * @Description:
 */
@Mapper
public interface LogDao extends BaseMapper<LogEntity> {
}
