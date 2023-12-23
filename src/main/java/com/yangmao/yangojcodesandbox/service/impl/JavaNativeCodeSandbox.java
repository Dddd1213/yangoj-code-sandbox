package com.yangmao.yangojcodesandbox.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yangmao.yangojcodesandbox.constant.FileConstant;
import com.yangmao.yangojcodesandbox.model.ExecuteCodeRequest;
import com.yangmao.yangojcodesandbox.model.ExecuteCodeResponse;
import com.yangmao.yangojcodesandbox.model.ExecuteMessage;
import com.yangmao.yangojcodesandbox.model.JudgeInfo;
import com.yangmao.yangojcodesandbox.service.CodeSandbox;
import com.yangmao.yangojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class JavaNativeCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        //默认先放成功，失败再改成3
        executeCodeResponse.setStatus(2);

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        //判断是否存在该目录，不存在则创建
        String userDir = System.getProperty(FileConstant.userDir);
        String globalCodePathName = userDir+ File.separator+FileConstant.globalCodePathName;
        log.info(globalCodePathName);
        if(!FileUtil.exist(globalCodePathName)){
            log.info("创建...");
            FileUtil.mkdir(globalCodePathName);
        }

        //用户提交的代码放入该目录中
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator+ FileConstant.ClassName;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        //编译代码，并通过错误码判断是否正常执行
        String compiledCmd = String.format("javac -encoding utf-8 %s",userCodeFile.getAbsolutePath());
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(compiledCmd);
        } catch (IOException e) {
            e.printStackTrace();
            return errorResponse(e);
        }
        ExecuteMessage compileExecuteMessage = ProcessUtils.doProcess(process,"编译");
        //如果编译错误，把错误信息放入message并返回
        if(StrUtil.isNotBlank(compileExecuteMessage.getErrorMessage())){
            executeCodeResponse.setMessage(compileExecuteMessage.getErrorMessage());
            executeCodeResponse.setStatus(3);
            //删除临时文件
            if(userCodeFile.getParentFile()!=null){
                FileUtil.del(userCodeParentPath);
            }
            return executeCodeResponse;
        }

        //遍历执行每一个inputList,获取输出结果executeMessageList
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for(String input : inputList){
            compiledCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s",userCodeParentPath,input);

            try {
                process = Runtime.getRuntime().exec(compiledCmd);
            } catch (IOException e) {
                e.printStackTrace();
                return errorResponse(e);
            }
            ExecuteMessage executeMessage = ProcessUtils.doProcess(process,"运行");
            executeMessageList.add(executeMessage);
        }

        //获取输出结果executeCodeResponse
        long maxTime = 0;
        List<String> outputList = new ArrayList<>();
        for(ExecuteMessage message:executeMessageList){
            //如果有错误信息
            if(StrUtil.isNotBlank(message.getErrorMessage())){
                executeCodeResponse.setMessage(message.getErrorMessage());
                executeCodeResponse.setStatus(3);
                //删除临时文件
                if(userCodeFile.getParentFile()!=null){
                    FileUtil.del(userCodeParentPath);
                }
                return executeCodeResponse;
            }
            outputList.add(message.getMessage());
            if(message.getTime()!=null){
                maxTime=Math.max(maxTime,message.getTime());
            }
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        //todo message是enum，需要在判题机那边放;memory需要借用第三方库，很麻烦
        judgeInfo.setTime(maxTime);
//        judgeInfo.setMemory();
        executeCodeResponse.setJudgeInfo(judgeInfo);

        //删除临时文件
        if(userCodeFile.getParentFile()!=null){
           FileUtil.del(userCodeParentPath);
        }
        return executeCodeResponse;
    }

    private ExecuteCodeResponse errorResponse(Throwable e){
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setJudgeInfo(null);
        executeCodeResponse.setStatus(4);
        executeCodeResponse.setMessage(e.getMessage());
        return executeCodeResponse;
    }

}
