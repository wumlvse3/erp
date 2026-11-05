
package com.jcloud.erp.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.erp.epd.entity.EpdItemMasterHistory;
import com.jcloud.system.utils.R;

import java.util.List;
import java.util.Map;

public interface AdditemHistoryService extends IService<EpdItemMasterHistory> {
    public R addObj(List<List<Map<String,Object>>> params);

    public R insertHistoryObj(EpdItemMasterHistory listData);

    public  R copyHistoryObj(EpdItemMasterHistory params);

}
