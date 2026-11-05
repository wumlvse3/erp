package com.jcloud.easypoi.excel.service;

import com.jcloud.easypoi.excel.entity.OrdGroupImport;
import com.jcloud.system.utils.R;
import java.util.List;

public interface OrdGroupImportService {

    /**
     * 处理并导入Excel数据
     * @param dataList 成功解析的数据列表
     * @param failList 校验失败的数据列表
     * @param isVerifyFail 是否有校验失败的数据
     * @return 导入结果
     */
    R importData(List<OrdGroupImport> dataList, List<OrdGroupImport> failList, boolean isVerifyFail);

}
