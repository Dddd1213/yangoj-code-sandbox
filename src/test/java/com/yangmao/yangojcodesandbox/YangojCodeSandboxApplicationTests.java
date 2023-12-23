package com.yangmao.yangojcodesandbox;

import cn.hutool.core.io.FileUtil;
import com.yangmao.yangojcodesandbox.model.ExecuteCodeRequest;
import com.yangmao.yangojcodesandbox.service.impl.JavaNativeCodeSandbox;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@SpringBootTest
class YangojCodeSandboxApplicationTests {

    @Autowired
    JavaNativeCodeSandbox javaNativeCodeSandbox;
    @Test
    void testSimple() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2","1 3"));
        String code = FileUtil.readString("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");

        javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
    @Test
    void testSleep() {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2","1 3"));
        String code = FileUtil.readString("testCode/sleepError/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");

        javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }

}
