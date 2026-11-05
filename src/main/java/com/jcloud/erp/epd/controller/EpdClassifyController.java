package com.jcloud.erp.epd.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/epdClassify")
public class EpdClassifyController {
    @Autowired
    SqlService sqlService;

    @GetMapping("/tree")
    @ResponseBody

    R getClassifyTree(@RequestParam("inv_org") String inv_org) {
        List<Map<String, Object>> all = sqlService.getAll("select classify_code,classify_name,classify_parent_code FROM (select type_code as classify_code,type_name as classify_name,'' as classify_parent_code,inv_org from epd_classify UNION select classify_code,classify_name, case when classify_parent_code ='' then case type_code When 'TJ' then 'TJ' When 'CG' then 'CG' When 'XS' then 'XS' When 'ZL' then 'ZL' When 'KC' then 'KC' When 'CW' then 'CW' When 'GJJ' then 'GJJ' END ELSE classify_parent_code END AS classify_parent_code ,inv_org from epd_classify ) temp WHERE inv_org='"+inv_org+"'");

        List<Map<String, Object>> tree = buildClassifyTree(all);

        return R.ok().setdata(tree);
    }

    private List<Map<String, Object>> buildClassifyTree(List<Map<String, Object>> all) {
        List<Map<String, Object>> tree = new ArrayList<>();

        // First pass: create tree nodes
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> classify : all) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", classify.get("classify_code"));
            node.put("label", classify.get("classify_code")+" "+classify.get("classify_name"));
            node.put("opened", true);
            node.put("checked", true);
            node.put("attributes", null);
            node.put("level", 0); // Will be set later
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(String.valueOf(classify.get("classify_code")), node);
        }

        // Second pass: build hierarchy
        for (Map<String, Object> classify : all) {
            String classifyId = String.valueOf(classify.get("classify_code"));
            Object parentIdObj = classify.get("classify_parent_code");
            String parentId = parentIdObj != null ? String.valueOf(parentIdObj) : "";

            Map<String, Object> node = nodeMap.get(classifyId);
            if ("".equals(parentId) || parentIdObj == null) {
                // Root node
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
}
