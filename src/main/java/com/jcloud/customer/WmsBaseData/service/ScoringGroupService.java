package com.jcloud.customer.WmsBaseData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.customer.WmsBaseData.entity.ScoringGroup;
import com.jcloud.customer.WmsBaseData.entity.ScoringModelDetail;
import com.jcloud.system.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-06-16
 */
public interface ScoringGroupService extends IService<ScoringGroup> {
    public R save( Map<String,Object> params);

}
