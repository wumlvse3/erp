package com.jcloud.timedTask.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.StatusLine;

/**
 * @author banana
 * @date 2020/9/10
 * @time 16:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResultBean {
    // http返回体
    private String httpEntity;
    // 返回状态
    private StatusLine statusLine;

    // 返回的请求头
    private String headers;

}
