package com.jcloud.customer.WmsBaseData.controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tree")
public class TreeController {
    @Autowired
    SqlService sqlService;

    @GetMapping("/org")
    @ResponseBody
    R test2222() {
        List<Map<String, Object>> all = sqlService.getAll("select org_id,org_code,org_name,parent_org_id from sys_org where org_type = 'ORG'");

        List<Map<String, Object>> tree = buildOrgTree(all);

        return R.ok().setdata(tree);
    }



    private List<Map<String, Object>> buildOrgTree(List<Map<String, Object>> all) {
        List<Map<String, Object>> tree = new ArrayList<>();

        // First pass: create tree nodes
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> org : all) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", org.get("org_id"));
            node.put("label", org.get("org_name"));
            node.put("opened", true);
            node.put("checked", true);
            node.put("attributes", null);
            node.put("level", 0); // Will be set later
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(String.valueOf(org.get("org_id")), node);
        }

        // Second pass: build hierarchy
        for (Map<String, Object> org : all) {
            String orgId = String.valueOf(org.get("org_id"));
            Object parentIdObj = org.get("parent_org_id");
            String parentId = parentIdObj != null ? String.valueOf(parentIdObj) : "";

            Map<String, Object> node = nodeMap.get(orgId);
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

