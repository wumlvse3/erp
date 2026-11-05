
package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.EpdRoutingHeadHistory;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface AddroutingHistoryService extends IService<EpdRoutingHeadHistory> {
    public R delHistoryObj(List<List<Map<String,Object>>> params);

    public R insertHistoryObj(EpdRoutingHeadHistory listData);


}
