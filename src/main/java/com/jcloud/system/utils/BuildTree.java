package com.jcloud.system.utils;

import com.jcloud.system.entity.Tree;
import com.jcloud.system.entity.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树状图生成工具
 *
 * @author yang
 * @org
 */
public class BuildTree {

    /**
     * 自带顶部（顶级节点）
     *
     * @param nodes
     * @param <T>
     * @return
     */
    public static <T> Tree<T> build(List<Tree<T>> nodes) {

        if (nodes == null) {
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();

        for (Tree<T> children : nodes) {
            String pid = children.getParentId();
            if (pid == null || "0".equals(pid)) {
                topNodes.add(children);
                continue;
            }
            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }
        }
        Tree<T> root = new Tree<T>();
        if (topNodes.size() == 1) {
            root = topNodes.get(0);
        } else {
            root.setId("-1");
            root.setParentId("");
            root.setHasParent(false);
            root.setChildren(true);
            root.setChecked(true);
            root.setChildren(topNodes);
            root.setText("顶级节点");
            Map<String, Object> state = new HashMap<>(16);
            root.setOpened(true);
        }

        return root;
    }


    /**
     * 自带顶部（顶级节点）
     *
     * @param nodes
     * @param topId 顶部id
     * @param <T>
     * @return
     */
    public static <T> Tree<T> buildTop(List<Tree<T>> nodes, String topId) {

        if (nodes == null) {
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();

        for (Tree<T> children : nodes) {
            String pid = children.getParentId();
            if (pid == null || topId.equals(children.getId()) || "".equals(pid)) {
                topNodes.add(children);
                continue;
            }
            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }
        }
        Tree<T> root = new Tree<T>();
        if (topNodes.size() == 1) {
            root = topNodes.get(0);
        } else {
            root.setId("-1");
            root.setParentId("");
            root.setHasParent(false);
            root.setChildren(true);
            root.setChecked(true);
            root.setChildren(topNodes);
            root.setText("顶级节点");
            Map<String, Object> state = new HashMap<>(16);
            root.setOpened(true);
        }

        return root;
    }


    /**
     * 可以定制顶级接口名字
     *
     * @param nodes
     * @param name  顶级节点名字
     * @param <T>
     * @return yang
     */
    public static <T> Tree<T> build(List<Tree<T>> nodes, String name) {

        if (nodes == null) {
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();

        for (Tree<T> children : nodes) {

            String pid = children.getParentId();
            if (pid == null || "0".equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }

        }

        Tree<T> root = new Tree<T>();

        root.setId("0");
        root.setParentId("");
        root.setHasParent(false);
        root.setChildren(true);
        root.setChecked(false);
        root.setChildren(topNodes);
        root.setText(name);
        Map<String, Object> state = new HashMap<>(16);
        root.setOpened(true);

        return root;
    }

    /**
     * 可以定制顶级接口名字与样式
     *
     * @param nodes
     * @param name  名字
     * @param icon  样式
     * @param <T>
     * @return
     */
    public static <T> Tree<T> build(List<Tree<T>> nodes, String name, String icon) {

        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();
        for (Tree<T> children : nodes) {
            String pid = children.getParentId();
            if (pid == null || "0".equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }
        }
        Tree<T> root = new Tree<T>();

        root.setId("0");
        root.setParentId("");
        root.setHasParent(false);
        root.setChildren(true);
        root.setChecked(true);
        root.setChildren(topNodes);
        root.setText(name);
        root.setIcon(icon);
        Map<String, Object> state = new HashMap<>(16);
        root.setOpened(true);
        return root;
    }

    /**
     * 可以定制顶级接口名字与样式
     *
     * @param nodes
     * @param name  名字
     * @param icon  样式
     * @param <T>
     * @return
     */
    public static <T> Tree<T> build(List<Tree<T>> nodes, String name, String icon, String ParentId) {

        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();
        for (Tree<T> children : nodes) {
            String pid = children.getParentId();
            if (pid == null || ParentId.equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }
        }
        Tree<T> root = new Tree<T>();

        root.setId(ParentId);
        root.setParentId("");
        root.setHasParent(false);
        root.setChildren(true);
        root.setChecked(true);
        root.setChildren(topNodes);
        root.setText(name);
        root.setIcon(icon);
        Map<String, Object> state = new HashMap<>(16);
        root.setOpened(true);
        return root;
    }

    /**
     * 可以定制顶部
     *
     * @param nodes
     * @param root
     * @param <T>
     * @return
     */
    public static <T> Tree<T> build(List<Tree<T>> nodes, Tree<T> root) {
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();
        for (Tree<T> children : nodes) {
            String pid = children.getParentId();
            if (pid == null || "0".equals(pid)) {
                topNodes.add(children);
                continue;
            }
            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);
                    continue;
                }
            }
        }
        root.setHasParent(false);
        root.setChildren(true);
        root.setChecked(true);
        root.setChildren(topNodes);
        return root;
    }


    /**
     * 没有顶部
     *
     * @param nodes   数据
     * @param idParam 特定id为顶部（如果没有传入空字符串）
     * @param <T>
     * @return
     */
    public static <T> List<Tree<T>> buildNoTop(List<Tree<T>> nodes, String idParam) {
        if (nodes == null) {
            return null;
        }
        List<Tree<T>> topNodes = new ArrayList<Tree<T>>();

        for (Tree<T> children : nodes) {

            String pid = children.getParentId();
            if (pid == null || idParam.equals(pid)) {
                topNodes.add(children);

                continue;
            }

            for (Tree<T> parent : nodes) {
                String id = parent.getId();
                if (id != null && id.equals(pid)) {
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);

                    continue;
                }
            }

        }
        return topNodes;
    }

    /**
     * 和tree的字段名不一样
     *
     * @param treeNodes
     * @param idParam
     * @return
     */
    public static List<TreeNode> buildJsonList(List<TreeNode> treeNodes, String idParam) {
        List<TreeNode> topNodes = new ArrayList<TreeNode>();

        for (TreeNode children : treeNodes) {


            String pid = children.getParentId();
            if (pid == null || idParam.equals(pid)) {
                topNodes.add(findChildren(children, treeNodes));

                continue;
            }

        }
        return topNodes;
    }

    /**
     * 递归查找子节点和buildJsonList组合使用，查询出所有子节点，效率会慢点
     *
     * @param treeNodes
     * @return
     */
    public static TreeNode findChildren(TreeNode treeNode, List<TreeNode> treeNodes) {
        for (TreeNode it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<TreeNode>());
                }
                treeNode.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

}