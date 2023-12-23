package com.yangmao.yangojcodesandbox.model;

import lombok.Data;

/**
 * process执行信息
 */
@Data
public class ExecuteMessage {

    /**
     * 退出返回值
     */
    private Integer exitValue;

    /**
     * 正常执行信息
     */
    private String message;

    /**
     * 错误执行信息
     */
    private String errorMessage;

    /**
     * 程序执行时间
     */
    private Long time;

}
