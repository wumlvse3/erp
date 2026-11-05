package com.jcloud.easypoi.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PoiReflectorUtil {

    private static final Map<Class<?>, PoiReflectorUtil> CACHE_REFLECTOR = new ConcurrentHashMap<>();

    private Map<String, Method> getMethods = new HashMap<>();
    private Map<String, Method> setMethods = new HashMap<>();
    private Map<String, Field> publicField = new HashMap<>();
    private Map<String, Method> enumMethods = new HashMap<>();
    private List<Field> fieldList = new ArrayList<>();
    private Class<?> type;

    private PoiReflectorUtil(Class<?> clazz) {
        this.type = clazz;
        addGetMethods(clazz);
        addFields(clazz);
        addSetMethods(clazz);
    }

    public static PoiReflectorUtil fromCache(Class<?> clazz) {
        if (!CACHE_REFLECTOR.containsKey(clazz)) {
            CACHE_REFLECTOR.put(clazz, new PoiReflectorUtil(clazz));
        }
        return CACHE_REFLECTOR.get(clazz);
    }

    private void addGetMethods(Class<?> cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0) {
                String propName = name.substring(3, 4).toLowerCase() + name.substring(4);
                getMethods.put(propName, method);
            } else if (name.startsWith("is") && name.length() > 2 && method.getParameterTypes().length == 0) {
                String propName = name.substring(2, 3).toLowerCase() + name.substring(3);
                getMethods.put(propName, method);
            }
        }
        if (cls.getSuperclass() != null && cls.getSuperclass() != Object.class) {
            addGetMethods(cls.getSuperclass());
        }
    }

    private void addSetMethods(Class<?> cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3 && method.getParameterTypes().length == 1) {
                String propName = name.substring(3, 4).toLowerCase() + name.substring(4);
                setMethods.put(propName, method);
            }
        }
        if (cls.getSuperclass() != null && cls.getSuperclass() != Object.class) {
            addSetMethods(cls.getSuperclass());
        }
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!"serialVersionUID".equals(field.getName())) {
                try {
                    field.setAccessible(true);
                    fieldList.add(field);
                    if (Modifier.isPublic(field.getModifiers())) {
                        publicField.put(field.getName(), field);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    public Object getValue(Object obj, String property) {
        Method m = getMethods.get(property);
        if (m != null) {
            try {
                return m.invoke(obj);
            } catch (Exception e) {
                // ignore
            }
        }
        Field f = publicField.get(property);
        if (f != null) {
            try {
                return f.get(obj);
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
        return null;
    }

    public boolean setValue(Object obj, String property, Object value) {
        Field f = publicField.get(property);
        if (f != null) {
            try {
                f.set(obj, value);
                return true;
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
        Method m = setMethods.get(property);
        if (m != null) {
            try {
                m.invoke(obj, value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public Method getSetMethod(String property) {
        return setMethods.get(property);
    }

    public Method getGetMethod(String property) {
        return getMethods.get(property);
    }

    public Object execEnumStaticMethod(String staticMethod, Object params) {
        if (!enumMethods.containsKey(staticMethod)) {
            try {
                enumMethods.put(staticMethod, type.getMethod(staticMethod, params.getClass()));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(
                        "Could not find static method [" + staticMethod + "] in [" + type.getName() + "]", e);
            }
        }
        try {
            return enumMethods.get(staticMethod).invoke(null, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
