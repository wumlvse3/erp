package com.jcloud.erp.epd.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 自定义反序列化器，兼容前端传入的布尔值(true/false)和整数值(0/1)
 * 将布尔值转换为对应的整数：true -> 1, false -> 0
 */
public class BooleanOrIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        if (token == JsonToken.VALUE_TRUE) {
            return 1;
        } else if (token == JsonToken.VALUE_FALSE) {
            return 0;
        } else if (token == JsonToken.VALUE_NUMBER_INT) {
            return p.getIntValue();
        } else if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        // 兜底：尝试获取文本值再转换
        String text = p.getText();
        if ("true".equalsIgnoreCase(text)) return 1;
        if ("false".equalsIgnoreCase(text)) return 0;
        return Integer.parseInt(text);
    }
}
