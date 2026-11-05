package com.jcloud.epd.Controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.*;

@RestController
@RequestMapping("/epdBom")
public class EpdBomController {
    @Autowired
    SqlService sqlService;

    @GetMapping("/tree")
    @ResponseBody
    R getBomTree(@RequestParam("inv_org") String inv_org,String parent_code) {
        String randsql="";
        if(parent_code==null ||"".equals(parent_code)){
            randsql="select component_code,component_name,parent_code FROM (SELECT t.parent_code,t.component_code AS component_code,CONCAT('[',IFNULL(t6.draw_no,''),'][',IFNULL(t6.item_name,''),'][',IFNULL(t.usage_factor,''),'][',IFNULL(t4.name,''),'][',IFNULL((SELECT GROUP_CONCAT(IFNULL(t5.op_abb,t5.op_name) SEPARATOR '-') FROM epd_routing_detail t5 WHERE t5.routing_no=t6.routing_no),''),']') AS component_name,t.inv_org FROM epd_bom t LEFT JOIN epd_item_master t6 ON t.component_code=t6.item_code AND t.inv_org=t6.inv_org LEFT JOIN sys_dept t4 ON t6.dept_code=t4.dept_code) temp WHERE inv_org='"+inv_org+"'";
            List<Map<String, Object>> all = sqlService.getAll(randsql);
            List<Map<String, Object>> tree = buildClassifyTree(all);
            return R.ok().setdata(tree);
        }else {
            randsql="WITH RECURSIVE bom_recursive AS (SELECT component_code, parent_code, inv_org, 0 AS bom_level FROM epd_bom WHERE inv_org = '"+inv_org+"' AND component_code = '"+parent_code+"' UNION ALL SELECT t.component_code, t.parent_code, t.inv_org, br.bom_level + 1 FROM epd_bom t INNER JOIN bom_recursive br ON t.parent_code = br.component_code WHERE t.inv_org = '"+inv_org+"') SELECT * FROM bom_recursive";
            List<Map<String, Object>> all = sqlService.getAll(randsql);
            List<Map<String, Object>> tree = buildBomTree(all, parent_code);
            return R.ok().setdata(tree);
        }


    }


    @GetMapping("/audit")
    @ResponseBody
    public R  getBomAudit(@RequestParam("inv_org") String inv_org,String parent_code) {
        // 查询BOM结构，包含物料有效性、自制/采购标记
        String randsql="WITH RECURSIVE BomStructure AS (SELECT t.parent_code, t.component_code, t.inv_org, 0 AS level, CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t WHERE t.inv_org = '"+inv_org+"' AND t.parent_code = '"+parent_code+"' UNION ALL SELECT t.parent_code, t.component_code, t.inv_org, bs.level + 1, CONCAT(bs.sort_path, '.', t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code = bs.component_code WHERE t.inv_org = '"+inv_org+"'), FinalResult AS (SELECT t.uu_id,t.parent_code, t.component_code,t.inv_org, t2.valid_flag as parent_valid_flag, t2.m_p_flag as parent_m_p_flag, t6.valid_flag as component_valid_flag, t6.m_p_flag as component_m_p_flag, bs.level, bs.sort_path FROM BomStructure bs JOIN epd_bom t ON bs.parent_code = t.parent_code AND bs.component_code = t.component_code AND bs.inv_org = t.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code = t2.item_code AND t2.inv_org = t.inv_org LEFT JOIN epd_item_master t6 ON t.component_code = t6.item_code AND t.inv_org = t6.inv_org ) SELECT * FROM FinalResult ORDER BY sort_path ";
        List<Map<String, Object>> allStat = sqlService.getAll(randsql);

        StringBuilder errorMsg = new StringBuilder();

        // 检查1：父子项代码在物料定义表中不能不存在，且需要为有效物料
        for (Map<String, Object> row : allStat) {
            String parentCode = row.get("parent_code") != null ? row.get("parent_code").toString() : "";
            String componentCode = row.get("component_code") != null ? row.get("component_code").toString() : "";
            String parentValidFlag = row.get("parent_valid_flag") != null ? row.get("parent_valid_flag").toString() : "";
            String componentValidFlag = row.get("component_valid_flag") != null ? row.get("component_valid_flag").toString() : "";

            // 检查父项是否存在且有效
            if (parentValidFlag == null || parentValidFlag.isEmpty()) {
                errorMsg.append("父项物料代码 ").append(parentCode).append(" 在物料定义表中不存在；");
            } else if (!"1".equals(parentValidFlag)) {
                errorMsg.append("父项物料代码 ").append(parentCode).append(" 不是有效物料；");
            }

            // 检查子项是否存在且有效
            if (componentValidFlag == null || componentValidFlag.isEmpty()) {
                errorMsg.append("子项物料代码 ").append(componentCode).append(" 在物料定义表中不存在；");
            } else if (!"1".equals(componentValidFlag)) {
                errorMsg.append("子项物料代码 ").append(componentCode).append(" 不是有效物料；");
            }
        }

        // 检查2：叶子节点需要是采购件(P)，其它节点需要是自制件(M)
        // 找出所有有子节点的component_code（非叶子节点）
        Set<String> nonLeafComponents = new HashSet<>();
        for (Map<String, Object> row : allStat) {
            String parentCode = row.get("parent_code") != null ? row.get("parent_code").toString() : "";
            if (!parentCode.isEmpty()) {
                nonLeafComponents.add(parentCode);
            }
        }

        for (Map<String, Object> row : allStat) {
            String componentCode = row.get("component_code") != null ? row.get("component_code").toString() : "";
            String componentMPFlag = row.get("component_m_p_flag") != null ? row.get("component_m_p_flag").toString() : "";
            boolean isLeaf = !nonLeafComponents.contains(componentCode);
            if (isLeaf) {
                if (!"P".equals(componentMPFlag)) {
                    errorMsg.append("叶子节点物料代码 ").append(componentCode).append(" 应该是采购件(P)，当前为 ").append(componentMPFlag).append("；");
                }
            } else {
                // 非叶子节点需要是自制件(M)
                if (!"M".equals(componentMPFlag)) {
                    errorMsg.append("非叶子节点物料代码 ").append(componentCode).append(" 应该是自制件(M)，当前为 ").append(componentMPFlag).append("；");
                }
            }
        }

        for (Map<String, Object> row : allStat) {
            String sortPath = row.get("sort_path") != null ? row.get("sort_path").toString() : "";
            String[] codes = sortPath.split("\\.");
            Set<String> seen = new HashSet<>();
            for (String code : codes) {
                if (!seen.add(code)) {
                    errorMsg.append("存在递归循环嵌套错误，物料代码 ").append(code).append(" 在路径中重复出现；");
                    break;
                }
            }
        }

        // 如果有错误，返回失败原因
        if (errorMsg.length() > 0) {
            return R.error("多层确认失败：" + errorMsg.toString());
        }

        // 检查都通过，更新epd_item_master中所有涉及的父子项物料的integrity_flag为Y
        Set<String> allItemCodes = new HashSet<>();
        for (Map<String, Object> row : allStat) {
            String pc = row.get("parent_code") != null ? row.get("parent_code").toString() : "";
            String cc = row.get("component_code") != null ? row.get("component_code").toString() : "";
            if (!pc.isEmpty()) allItemCodes.add(pc);
            if (!cc.isEmpty()) allItemCodes.add(cc);
        }
        if (!allItemCodes.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (String code : allItemCodes) {
                if (inClause.length() > 0) inClause.append(",");
                inClause.append("'").append(code).append("'");
            }
            String updateSql = "UPDATE epd_item_master SET integrity_flag = 'Y' WHERE item_code IN (" + inClause.toString() + ") AND inv_org = '" + inv_org + "'";
            sqlService.updateOne(updateSql);
        }

        return R.ok("多层确认成功");
    }

    @GetMapping("/unaudit")
    @ResponseBody
    public R  getBomUnaudit(@RequestParam("inv_org") String inv_org,String parent_code) {
        // 查询BOM结构，包含物料有效性、自制/采购标记
        String randsql="WITH RECURSIVE BomStructure AS (SELECT t.parent_code, t.component_code, t.inv_org, 0 AS level, CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t WHERE t.inv_org = '"+inv_org+"' AND t.parent_code = '"+parent_code+"' UNION ALL SELECT t.parent_code, t.component_code, t.inv_org, bs.level + 1, CONCAT(bs.sort_path, '.', t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code = bs.component_code WHERE t.inv_org = '"+inv_org+"'), FinalResult AS (SELECT t.uu_id,t.parent_code, t.component_code,t.inv_org, t2.valid_flag as parent_valid_flag, t2.m_p_flag as parent_m_p_flag, t6.valid_flag as component_valid_flag, t6.m_p_flag as component_m_p_flag, bs.level, bs.sort_path FROM BomStructure bs JOIN epd_bom t ON bs.parent_code = t.parent_code AND bs.component_code = t.component_code AND bs.inv_org = t.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code = t2.item_code AND t2.inv_org = t.inv_org LEFT JOIN epd_item_master t6 ON t.component_code = t6.item_code AND t.inv_org = t6.inv_org ) SELECT * FROM FinalResult ORDER BY sort_path ";
        List<Map<String, Object>> allStat = sqlService.getAll(randsql);
        Set<String> allItemCodes = new HashSet<>();
        for (Map<String, Object> row : allStat) {
            String pc = row.get("parent_code") != null ? row.get("parent_code").toString() : "";
            String cc = row.get("component_code") != null ? row.get("component_code").toString() : "";
            if (!pc.isEmpty()) allItemCodes.add(pc);
            if (!cc.isEmpty()) allItemCodes.add(cc);
        }
        if (!allItemCodes.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (String code : allItemCodes) {
                if (inClause.length() > 0) inClause.append(",");
                inClause.append("'").append(code).append("'");
            }
            String updateSql = "UPDATE epd_item_master SET integrity_flag = 'N' WHERE item_code IN (" + inClause.toString() + ") AND inv_org = '" + inv_org + "'";
            sqlService.updateOne(updateSql);
        }

        return R.ok("取消多层确认成功");
    }

    @RequestMapping(value = "getData", method = RequestMethod.GET)
    public R getData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            String parent_code_new=ObjectUtils.oGetS(queryMap.get("parent_code_new"));
            String component_code_new=ObjectUtils.oGetS(queryMap.get("component_code_new"));
            String query_flag=ObjectUtils.oGetS(queryMap.get("query_flag"));
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            try {
                StringBuffer condStrAll = new StringBuffer();
                condStrAll.append(" and 1=1 ");
                String inv_org="";
                String parent_code="";
                if (queryList != null && !queryList.isEmpty()) {
                    for (Map<String, Object> queryKey : queryList) {
                        if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                            String condStr =" ";
                            if(queryKey.get("field").toString().equals("inv_org") )
                            {
                                inv_org=(String)queryKey.get("value");

                            } else if( queryKey.get("field").toString().equals("parent_code")) {
                                parent_code = (String)queryKey.get("value");
                            }
                            condStrAll.append(condStr);
                        }
                    }
                }
                String selAll ="";
                if (!parent_code.isEmpty()&&"S".equals(query_flag)&&!"".equals(parent_code)){
                    selAll = "WITH RECURSIVE BomStructure AS (SELECT t.parent_code, t.component_code, t.inv_org, 0 AS level, CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t WHERE t.inv_org = '"+inv_org+"' AND t.parent_code = '"+parent_code+"' UNION ALL SELECT t.parent_code, t.component_code, t.inv_org, bs.level + 1, CONCAT(bs.sort_path, '.', t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code = bs.component_code WHERE t.inv_org = '"+inv_org+"'), FinalResult AS (SELECT t.uu_id, t.group_id, t.create_user_id, t.create_time, t.modify_user_id, t.modify_time, t.flow_id, t.is_deleted, t.parent_code, t.component_code, t.effective_date, t.discon_date, t.offset_time, CONCAT(t.scrap_rate, '%') as scrap_rate, t.usage_factor, t.backflush_flag, t.backflush_warehouse, t.backflush_bin, t.delivery_location, t.remark, t.project_no, t.pick_flag, t.inv_org, t2.item_name, t2.draw_no, t3.unit_name, t2.lead_time, t4.name as detp_name, t5.op_abb, t6.item_name as component_name, t6.draw_no as component_draw_no, t7.unit_name as component_unit_name, t6.m_p_flag, t6.integrity_flag, t6.routing_no as routing_no, bs.level, bs.sort_path FROM BomStructure bs JOIN epd_bom t ON bs.parent_code = t.parent_code AND bs.component_code = t.component_code AND bs.inv_org = t.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code = t2.item_code AND t2.inv_org = t.inv_org LEFT JOIN epd_unit_measure t3 ON t2.stock_unit = t3.unit_id LEFT JOIN sys_dept t4 ON t2.dept_code = t4.dept_code LEFT JOIN epd_item_master t6 ON t.component_code = t6.item_code AND t.inv_org = t6.inv_org LEFT JOIN epd_routing_detail t5 ON t5.routing_no = t6.routing_no LEFT JOIN epd_unit_measure t7 ON t6.stock_unit = t7.unit_id) SELECT * FROM FinalResult ORDER BY sort_path ";
                }
                else if("T".equals(query_flag)&&!parent_code_new.isEmpty()&&!component_code_new.isEmpty()&&!"".equals(parent_code_new)&&!"".equals(component_code_new)){
                    selAll = "WITH RECURSIVE BomStructure AS (SELECT t.parent_code, t.component_code, t.inv_org, 0 AS level, CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t WHERE t.inv_org = '"+inv_org+"' AND t.parent_code = '"+parent_code_new+"' AND t.component_code = '"+component_code_new+"' UNION ALL SELECT t.parent_code, t.component_code, t.inv_org, bs.level + 1, CONCAT(bs.sort_path, '.', t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code = bs.component_code WHERE t.inv_org = '"+inv_org+"'), FinalResult AS (SELECT t.uu_id, t.group_id, t.create_user_id, t.create_time, t.modify_user_id, t.modify_time, t.flow_id, t.is_deleted, t.parent_code, t.component_code, t.effective_date, t.discon_date, t.offset_time, CONCAT(t.scrap_rate, '%') as scrap_rate, t.usage_factor, t.backflush_flag, t.backflush_warehouse, t.backflush_bin, t.delivery_location, t.remark, t.project_no, t.pick_flag, t.inv_org, t2.item_name, t2.draw_no, t3.unit_name, t2.lead_time, t4.name as detp_name, t5.op_abb, t6.item_name as component_name, t6.draw_no as component_draw_no, t7.unit_name as component_unit_name, t6.m_p_flag, t6.integrity_flag, t6.routing_no as routing_no, bs.level, bs.sort_path FROM BomStructure bs JOIN epd_bom t ON bs.parent_code = t.parent_code AND bs.component_code = t.component_code AND bs.inv_org = t.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code = t2.item_code AND t2.inv_org = t.inv_org LEFT JOIN epd_unit_measure t3 ON t2.stock_unit = t3.unit_id LEFT JOIN sys_dept t4 ON t2.dept_code = t4.dept_code LEFT JOIN epd_item_master t6 ON t.component_code = t6.item_code AND t.inv_org = t6.inv_org LEFT JOIN epd_routing_detail t5 ON t5.routing_no = t6.routing_no LEFT JOIN epd_unit_measure t7 ON t6.stock_unit = t7.unit_id) SELECT * FROM FinalResult ORDER BY sort_path ";
                }else if ("T".equals(query_flag)&&"".equals(parent_code_new)&&!"".equals(component_code_new)&&!component_code_new.isEmpty()){
                    selAll = "WITH RECURSIVE BomStructure AS (SELECT t.uu_id,t.group_id,t.create_user_id,t.create_time,t.modify_user_id,t.modify_time,t.flow_id,t.is_deleted,t.parent_code,t.component_code,t.effective_date,t.discon_date,t.offset_time,CONCAT(t.scrap_rate, '%') as scrap_rate,t.usage_factor,t.backflush_flag,t.backflush_warehouse,t.backflush_bin,t.delivery_location,t.remark,t.project_no,t.pick_flag,t.inv_org,CAST(NULL AS CHAR(200)) AS item_name,CAST(NULL AS CHAR(50)) AS draw_no,CAST(NULL AS CHAR(20)) AS unit_name,CAST(NULL AS DECIMAL(10,2)) AS lead_time,CAST(NULL AS CHAR(100)) AS detp_name,t5.op_abb,t6.item_name AS component_name,t6.draw_no AS component_draw_no,t7.unit_name AS component_unit_name,t6.m_p_flag,t6.integrity_flag,t6.routing_no AS routing_no,0 AS level,CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t LEFT JOIN epd_item_master t6 ON t.component_code=t6.item_code AND t.inv_org=t6.inv_org LEFT JOIN epd_routing_detail t5 ON t5.routing_no=t6.routing_no LEFT JOIN epd_unit_measure t7 ON t6.stock_unit=t7.unit_id WHERE t.component_code='"+component_code_new+"' AND t.inv_org='"+inv_org+"' AND t.parent_code IS NULL UNION ALL SELECT t.uu_id,t.group_id,t.create_user_id,t.create_time,t.modify_user_id,t.modify_time,t.flow_id,t.is_deleted,t.parent_code,t.component_code,t.effective_date,t.discon_date,t.offset_time,CONCAT(t.scrap_rate, '%') as scrap_rate,t.usage_factor,t.backflush_flag,t.backflush_warehouse,t.backflush_bin,t.delivery_location,t.remark,t.project_no,t.pick_flag,t.inv_org,t2.item_name,t2.draw_no,t3.unit_name,t2.lead_time,t4.name AS detp_name,t5.op_abb,t6.item_name AS component_name,t6.draw_no AS component_draw_no,t7.unit_name AS component_unit_name,t6.m_p_flag,t6.integrity_flag,t6.routing_no AS routing_no,bs.level+1,CONCAT(bs.sort_path,'.',t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code=bs.component_code AND t.inv_org=bs.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code=t2.item_code AND t2.inv_org=t.inv_org LEFT JOIN epd_unit_measure t3 ON t2.stock_unit=t3.unit_id LEFT JOIN sys_dept t4 ON t2.dept_code=t4.dept_code LEFT JOIN epd_item_master t6 ON t.component_code=t6.item_code AND t.inv_org=t6.inv_org LEFT JOIN epd_routing_detail t5 ON t5.routing_no=t6.routing_no LEFT JOIN epd_unit_measure t7 ON t6.stock_unit=t7.unit_id WHERE t.inv_org='"+inv_org+"') SELECT * FROM BomStructure ORDER BY sort_path ";
                }
                else if(parent_code.isEmpty()&&"S".equals(query_flag)&&"".equals(parent_code)){
                    selAll = "WITH RECURSIVE BomStructure AS (SELECT t.parent_code, t.component_code, t.inv_org, 0 AS level, CAST(t.component_code AS CHAR(200)) AS sort_path FROM epd_bom t WHERE t.inv_org = '"+inv_org+"' AND (t.parent_code IS NULL OR t.parent_code = '' OR t.parent_code NOT IN (SELECT DISTINCT component_code FROM epd_bom WHERE inv_org = '"+inv_org+"')) UNION ALL SELECT t.parent_code, t.component_code, t.inv_org, bs.level + 1, CONCAT(bs.sort_path, '.', t.component_code) AS sort_path FROM epd_bom t INNER JOIN BomStructure bs ON t.parent_code = bs.component_code WHERE t.inv_org = '"+inv_org+"'), FinalResult AS (SELECT t.uu_id, t.group_id, t.create_user_id, t.create_time, t.modify_user_id, t.modify_time, t.flow_id, t.is_deleted, t.parent_code, t.component_code, t.effective_date, t.discon_date, t.offset_time, CONCAT(t.scrap_rate, '%') as scrap_rate, t.usage_factor, t.backflush_flag, t.backflush_warehouse, t.backflush_bin, t.delivery_location, t.remark, t.project_no, t.pick_flag, t.inv_org, t2.item_name, t2.draw_no, t3.unit_name, t2.lead_time, t4.name as detp_name, t5.op_abb, t6.item_name as component_name, t6.draw_no as component_draw_no, t7.unit_name as component_unit_name, t6.m_p_flag, t6.integrity_flag, t6.routing_no as routing_no, bs.level, bs.sort_path FROM BomStructure bs JOIN epd_bom t ON bs.parent_code = t.parent_code AND bs.component_code = t.component_code AND bs.inv_org = t.inv_org LEFT JOIN epd_item_master t2 ON t.parent_code = t2.item_code AND t2.inv_org = t.inv_org LEFT JOIN epd_unit_measure t3 ON t2.stock_unit = t3.unit_id LEFT JOIN sys_dept t4 ON t2.dept_code = t4.dept_code LEFT JOIN epd_item_master t6 ON t.component_code = t6.item_code AND t.inv_org = t6.inv_org LEFT JOIN epd_routing_detail t5 ON t5.routing_no = t6.routing_no LEFT JOIN epd_unit_measure t7 ON t6.stock_unit = t7.unit_id) SELECT * FROM FinalResult ORDER BY sort_path ";
                }
                if(selAll== null||"".equals(selAll)){
                    return R.error("");
                }
                List<Map<String, Object>> allStat = sqlService.getAll(selAll);
                int total = allStat != null ? allStat.size() : 0;
                int offsetInt = Integer.parseInt(offset);
                int sizeInt = Integer.parseInt(size);
                if (allStat != null && offsetInt < allStat.size()) {
                    int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                    allStat = allStat.subList(offsetInt, endIndex);
                } else {
                    allStat = new ArrayList<>();
                }
                Map<String, Object> result = new HashMap<>();
                result.put("rows", allStat);
                result.put("total", total);
                return R.ok().setdata(result);
            } catch (Exception e) {
                e.printStackTrace();
                return R.error(e.getMessage());
            }
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    private List<Map<String, Object>> buildClassifyTree(List<Map<String, Object>> all) {
        List<Map<String, Object>> tree = new ArrayList<>();

        // First pass: create tree nodes
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> classify : all) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", classify.get("component_code"));
            node.put("label", classify.get("component_code")+" "+classify.get("component_name"));
            node.put("opened", true);
            node.put("checked", true);
            node.put("attributes", null);
            node.put("parentcode", classify.get("parent_code"));
            node.put("level", 0); // Will be set later
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(String.valueOf(classify.get("component_code")), node);
        }

        // Second pass: build hierarchy
        for (Map<String, Object> classify : all) {
            String classifyId = String.valueOf(classify.get("component_code"));
            Object parentIdObj = classify.get("parent_code");
            String parentId = parentIdObj != null ? String.valueOf(parentIdObj) : "";

            Map<String, Object> node = nodeMap.get(classifyId);
            if ("".equals(parentId) || parentIdObj == null) {
                tree.add(node);
                node.put("level", 0);
            } else {
                // Child node
                Map<String, Object> parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parentNode.get("children");
                    children.add(node);
                    // Set level based on parent level
                    int parentLevel = (Integer) parentNode.get("level");
                    node.put("level", parentLevel + 1);
                }
            }
        }

        return tree;
    }

    private List<Map<String, Object>> buildBomTree(List<Map<String, Object>> all, String parentCode) {
        List<Map<String, Object>> tree = new ArrayList<>();

        // 创建根节点（传入的父项）
        Map<String, Object> rootNode = new HashMap<>();
        rootNode.put("id", parentCode);
        rootNode.put("label", parentCode);
        rootNode.put("opened", true);
        rootNode.put("checked", true);
        rootNode.put("parentcode","");
        rootNode.put("attributes", null);
        rootNode.put("level", 0);
        rootNode.put("children", new ArrayList<Map<String, Object>>());

        if (all == null || all.isEmpty()) {
            tree.add(rootNode);
            return tree;
        }

        // 收集所有 component_code，用于判断根节点
        java.util.Set<String> componentCodes = new java.util.HashSet<>();
        for (Map<String, Object> classify : all) {
            componentCodes.add(String.valueOf(classify.get("component_code")));
        }

        // First pass: 用 parent_code + component_code 作为唯一 key
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> classify : all) {
            String componentCode = String.valueOf(classify.get("component_code"));
            String pCode = String.valueOf(classify.get("parent_code"));
            String key = pCode + "-" + componentCode;

            Map<String, Object> node = new HashMap<>();
            node.put("id", componentCode);
            Map<String, Object> componentName = sqlService.getOne("select component_name FROM (SELECT t.parent_code,t.component_code AS component_code,CONCAT('[',IFNULL(t6.draw_no,''),'][',IFNULL(t6.item_name,''),'][',IFNULL(t.usage_factor,''),'][',IFNULL(t4.name,''),'][',IFNULL((SELECT GROUP_CONCAT(IFNULL(t5.op_abb,t5.op_name) SEPARATOR '-') FROM epd_routing_detail t5 WHERE t5.routing_no=t6.routing_no),''),']') AS component_name,t.inv_org FROM epd_bom t LEFT JOIN epd_item_master t6 ON t.component_code=t6.item_code AND t.inv_org=t6.inv_org LEFT JOIN sys_dept t4 ON t6.dept_code=t4.dept_code) temp WHERE inv_org='"+classify.get("inv_org")+"' and component_code='"+componentCode+"' and parent_code='"+pCode+"'");
            String name = componentName != null && componentName.get("component_name") != null ? String.valueOf(componentName.get("component_name")) : "";
            node.put("label", componentCode + " " + name);
            node.put("opened", true);
            node.put("checked", true);
            node.put("parentcode", classify.get("parent_code"));
            node.put("attributes", null);
            node.put("level", 0);
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(key, node);
        }

        // Second pass: 构建层级关系
        java.util.Set<String> addedKeys = new java.util.HashSet<>(); // 用于去重
        for (Map<String, Object> classify : all) {
            String componentCode = String.valueOf(classify.get("component_code"));
            String pCode = String.valueOf(classify.get("parent_code"));
            String key = pCode + "-" + componentCode;

            // 去重：如果已经添加过，跳过
            if (addedKeys.contains(key)) {
                continue;
            }

            Map<String, Object> node = nodeMap.get(key);
            // 如果 parent_code 等于传入的父项，挂到根节点下
            if (parentCode.equals(pCode)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) rootNode.get("children");
                children.add(node);
                node.put("level", 1);
                addedKeys.add(key);
            } else if (componentCodes.contains(pCode)) {
                // 查找父节点
                boolean found = false;
                for (Map<String, Object> parent : all) {
                    String pc = String.valueOf(parent.get("component_code"));
                    if (pc.equals(pCode)) {
                        String ppCode = String.valueOf(parent.get("parent_code"));
                        String parentKey = ppCode + "-" + pc;
                        Map<String, Object> parentNode = nodeMap.get(parentKey);
                        if (parentNode != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> children = (List<Map<String, Object>>) parentNode.get("children");
                            children.add(node);
                            int parentLevel = (Integer) parentNode.get("level");
                            node.put("level", parentLevel + 1);
                            addedKeys.add(key);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    // 找不到父节点，挂到根节点下
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) rootNode.get("children");
                    children.add(node);
                    node.put("level", 1);
                    addedKeys.add(key);
                }
            }
        }

        tree.add(rootNode);
        return tree;
    }
}
