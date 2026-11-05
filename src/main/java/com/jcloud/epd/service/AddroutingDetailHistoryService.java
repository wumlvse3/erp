
package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.EpdRoutingDetailHistory;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface AddroutingDetailHistoryService extends IService<EpdRoutingDetailHistory> {
    public R delHistoryObj(List<List<Map<String,Object>>> params);

    public R insertHistoryObj(EpdRoutingDetailHistory listData);


}
