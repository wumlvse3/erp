package com.jcloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.system.entity.EmailModule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailModuleMapper extends BaseMapper<EmailModule> {
    EmailModule findByModuleCode(String moduleCode);
}