package com.jcloud.erp.ord.controller;

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
@RequestMapping("/ordSupclass")
public class OrdSupclassController {
    @Autowired
    SqlService sqlService;

    @GetMapping("/tree")
    @ResponseBody
    R getSupclassTree(@RequestParam("sales_org") String sales_org) {
        List<Map<String, Object>> all = sqlService.getAll("select class_code,class_name,pid_code from ord_supclass where sales_org ='"+sales_org+"'");

        List<Map<String, Object>> tree = buildSupclassTree(all);

        return R.ok().setdata(tree);
    }

    private List<Map<String, Object>> buildSupclassTree(List<Map<String, Object>> all) {
        List<Map<String, Object>> tree = new ArrayList<>();

        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> supclass : all) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", supclass.get("class_code"));
            node.put("label", supclass.get("class_code")+" "+supclass.get("class_name"));
            node.put("opened", true);
            node.put("checked", true);
            node.put("attributes", null);
            node.put("level", 0);
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(String.valueOf(supclass.get("class_code")), node);
        }

        for (Map<String, Object> supclass : all) {
            String supclassId = String.valueOf(supclass.get("class_code"));
            Object parentIdObj = supclass.get("pid_code");
            String parentId = parentIdObj != null ? String.valueOf(parentIdObj) : "";

            Map<String, Object> node = nodeMap.get(supclassId);
            if ("".equals(parentId) || parentIdObj == null) {
                tree.add(node);
                node.put("level", 0);
            } else {
                Map<String, Object> parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parentNode.get("children");
                    children.add(node);
                    int parentLevel = (Integer) parentNode.get("level");
                    node.put("level", parentLevel + 1);
                }
            }
        }

        return tree;
    }
}