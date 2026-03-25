package com.jcloud.system.utils;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Assert 工具类
 *
 * @author dada
 * @see
 */
public class AssertHelper {

    /**
     * Collection 内对象皆为非空
     *
     * @param collection
     * @param message
     */
    public static void allNotNull(Collection collection, String message) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            Object object = (Object) iterator.next();
            if (object == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Collection 内对象皆为非空
     *
     * @param collection
     * @see AssertHelper#allNotNull(Collection, String)
     */
    public static void allNotNull(Collection collection) {
        allNotNull(collection, "[Assertion failed] - these argument are required; they all cannot be null");
    }

    /**
     * Arrays 内对象皆为非空
     *
     * @param objects
     * @param message
     */
    public static void allNotNull(Object[] objects, String message) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Arrays 内对象皆为非空
     *
     * @param objects
     * @see AssertHelper#allNotNull(Object[], String)
     */
    public static void allNotNull(Object[] objects) {
        allNotNull(objects, "[Assertion failed] - these argument are required; they all cannot be null");
    }

    /**
     * Collection 内对象皆为非空且有一非空格的字母
     *
     * @param collection String对象集合
     * @param message    异常message
     */
    public static void allHasText(Collection collection, String message) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String str = (String) iterator.next();
            if (!StringUtils.hasText(str)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Collection 内对象皆为非空且有一非空格的字母
     *
     * @param collection String对象集合
     */
    public static void allHasText(Collection collection) {
        allHasText(collection, "[Assertion failed] - these argument are required and hasText;");
    }

    /**
     * Arrays 内对象皆为非空且有一非空格的字母
     *
     * @param strs    String数组
     * @param message
     */
    public static void allHasText(String[] strs, String message) {
        for (int i = 0; i < strs.length; i++) {
            if (!StringUtils.hasText(strs[i])) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Arrays 内对象皆为非空且有一非空格的字母
     *
     * @param strs String数组
     */
    public static void allHasText(String[] strs) {
        allHasText(strs, "[Assertion failed] - these argument are required and hasText;");
    }

    /**
     * "",null等返回 false,否则返回true
     *
     * @param value
     * @return
     */
    public static Boolean notEmpty(Object value) {
        return !empty(value);
    }

    /**
     * 为空返回true
     *
     * @param value
     * @return
     */
    public static Boolean empty(Object value) {
        if (value == null || "".equals(value) || "ALL".equals(value) || "null".equals(value) || "NULL".equals(value) || "undefined".equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 不能为空判断,如果为null,""name会抛出异常
     *
     * @param value
     */
    public static void notEmpty_assert(Object value) {
        notEmpty_assert(value, "值为空");
    }

    /**
     * 非空判断,如果为Null,""返回false
     *
     * @author sqing
     */
    public static Boolean isOrNotEmpty_assert(Object value) {
        if (empty(value)) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 不能为空判断,如果为null,""name会抛出异常
     *
     * @param value 需要判断的值
     * @param erro  异常信息
     *              <strong>程序范例：</strong>
     *              AssertHelper.notEmpty_assert(value,"xxxx不能为空")
     */
    public static void notEmpty_assert(Object value, String erro) {
        if (empty(value)) {
            throw new RuntimeException(erro);
        }
    }
}
