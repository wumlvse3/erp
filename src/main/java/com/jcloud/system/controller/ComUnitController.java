package com.jcloud.system.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.erp.ord.service.OrdOrderHeadService;
import com.jcloud.erp.ord.service.OrdInvoiceHeadService;
import com.jcloud.system.event.ComUnitEntity;
import com.jcloud.system.service.ComUnitService;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元控制器 - 处理Excel导出等通用单元操作
 */
@RestController
@RequestMapping("/sys/unit")
public class ComUnitController {

    private static final Logger logger = LoggerFactory.getLogger(ComUnitController.class);

    @Autowired
    private ComUnitService comUnitService;

    @Autowired
    private SqlService sqlService;

    @Autowired
    private OrdOrderHeadService ordOrderHeadService;

    @Autowired
    private OrdInvoiceHeadService ordInvoiceHeadService;

    /**
     * Excel导出数据获取
     * 根据单元ID获取配置的SQL或路由路径，执行查询返回数据
     */
    @RequestMapping(value = "/getexceldata/{id}", method = RequestMethod.GET)
    public R getExcelData(@PathVariable("id") Long id, @RequestParam Map<String, Object> params) {
        try {
            // 获取单元配置
            ComUnitEntity unitEntity = comUnitService.getById(id);
            if (unitEntity == null) {
                return R.error("单元不存在: " + id);
            }

            String unitContent = ObjectUtils.oGetS(unitEntity.getUnitContent()).trim();
            if (unitContent.equals("")) {
                return R.error("单元SQL配置为空: " + id);
            }

            // 解析查询条件
            String yangQuery = ObjectUtils.oGetS(params.get("yang_query"));
            List<Map<String, Object>> queryList = new ArrayList<>();
            if (!yangQuery.equals("")) {
                yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
                queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            }

            // 判断是否为 /logic/ 路由路径
            if (unitContent.startsWith("/logic/")) {
                return handleLogicRoute(unitContent, queryList, params);
            }

            // 普通SQL处理：解析SQL并执行
            String parsedSql = comUnitService.getUnitSql(unitContent, params, false);
            logger.info("Excel export SQL: " + parsedSql);

            List<Map<String, Object>> dataList = sqlService.getAll(parsedSql);
            int total = dataList != null ? dataList.size() : 0;

            Map<String, Object> result = new HashMap<>();
            result.put("rows", dataList != null ? dataList : new ArrayList<>());
            result.put("total", total);

            return R.ok().setdata(result);
        } catch (Exception e) {
            logger.error("Excel导出数据获取失败", e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 处理 /logic/ 路由路径
     * 将 /logic/xxx/yyy 路由到对应的service方法
     */
    private R handleLogicRoute(String routePath, List<Map<String, Object>> queryList, Map<String, Object> params) {
        // 去掉 /logic/ 前缀
        String path = routePath.substring("/logic/".length());

        // Excel导出时不分页，获取全部数据
        String offset = "0";
        String size = "999999";

        // 根据路由路径调用对应service
        if (path.equals("ordOrderHead/getStatData")) {
            return ordOrderHeadService.getStatData(queryList, offset, size);
        } else if (path.equals("ordInvoiceHead/getStatData")) {
            return ordInvoiceHeadService.getStatData(queryList, offset, size);
        } else if (path.equals("ordInvoiceHead/getStatBackData")) {
            return ordInvoiceHeadService.getStatBackData(queryList, offset, size);
        } else {
            return R.error("不支持的路由路径: " + routePath);
        }
    }
}
