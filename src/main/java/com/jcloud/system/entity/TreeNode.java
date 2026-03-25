package com.jcloud.system.entity;


import java.util.List;

/**
 * 树结构
 * @Auther: yang
 * @Date: 2023/4/10 15:53
 * @Description:
 */
public class TreeNode {

    private String id;

    private String parentId;

    private String name;

    private List<TreeNode> children;


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
