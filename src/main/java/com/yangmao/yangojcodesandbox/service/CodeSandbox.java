package com.yangmao.yangojcodesandbox.service;

import com.yangmao.yangojcodesandbox.model.ExecuteCodeRequest;
import com.yangmao.yangojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
