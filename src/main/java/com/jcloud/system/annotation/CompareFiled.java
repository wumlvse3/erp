package com.jcloud.system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 供应商字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareFiled {
	/**字段展示形式**/
	String value() default "";
	/**如果是枚举类型对应枚举代码**/
	String enumCode() default "";
	/**是否核心信息**/
	boolean coreFlag() default false;
	/**是否是文件**/
	boolean isFile() default false;
}
