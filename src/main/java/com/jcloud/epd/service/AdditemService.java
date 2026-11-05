
package com.jcloud.epd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcloud.epd.entity.EpdItemMaster;
import com.jcloud.system.utils.R;
import java.util.List;
import java.util.Map;
public interface AdditemService extends IService<EpdItemMaster> {

    public R addOrgItems(List<List<Map<String, Object>>> list);
}
