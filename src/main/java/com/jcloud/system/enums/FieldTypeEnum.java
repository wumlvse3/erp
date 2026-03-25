package com.jcloud.system.enums;

public enum FieldTypeEnum {
    TEXT(0,"文本"),
    FILE(1,"文件"),
    ENUM(2,"枚举");
    private int _value;
    private String _name;

    FieldTypeEnum(int value, String name)
    {
        _value = value;
        _name = name;
    }
    public static FieldTypeEnum getByKey(int key){
        for (FieldTypeEnum value : FieldTypeEnum.values()) {
            if(value.getValue() == key){
                return value;
            }
        }
        return null;
    }
    public int getValue()
    {
        return _value;
    }

    public String getName()
    {
        return _name;
    }
}
