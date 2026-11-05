package com.jcloud.easypoi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {

    public String databaseFormat() default "yyyyMMddHHmmss";

    public String exportFormat() default "";

    public String importFormat() default "";

    public String format() default "";

    public String timezone() default "";

    public String numFormat() default "";

    @Deprecated
    public double height() default 10;

    public int imageType() default 1;

    public String suffix() default "";

    public boolean isWrap() default true;

    public int[] mergeRely() default {};

    public boolean mergeVertical() default false;

    public String name();

    public String groupName() default "";

    public boolean needMerge() default false;

    public String orderNum() default "0";

    public String[] replace() default {};

    public String dict() default  "";

    public boolean addressList() default false;

    public String savePath() default "/excel/upload/img";

    public int type() default 1;

    public boolean isStatistics() default false;

    public boolean isHyperlink() default false;

    public String isImportField() default "false";

    public int fixedIndex() default -1;

    public boolean isColumnHidden() default  false;

    public String enumExportField() default "";

    public String enumImportMethod() default "";

    public String   desensitizationRule() default "";

    /**
     * 是否唯一（导入时校验该列值不能重复）
     */
    public boolean unique() default false;

}
