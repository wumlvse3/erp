
package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.EpdBomHistory;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface AddbomHistoryService extends IService<EpdBomHistory> {
    public R delHistoryObj(List<List<Map<String,Object>>> params);

    public R insertHistoryObj(EpdBomHistory listData);

    public  R copyHistoryObj(EpdBomHistory params);

}
