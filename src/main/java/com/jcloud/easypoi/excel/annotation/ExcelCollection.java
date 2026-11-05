package com.jcloud.easypoi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCollection {

    public String id() default "";

    public String name();

    public String orderNum() default "0";

    public Class<?> type() default ArrayList.class;
}
