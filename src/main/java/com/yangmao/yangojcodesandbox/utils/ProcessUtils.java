package com.yangmao.yangojcodesandbox.utils;

import com.yangmao.yangojcodesandbox.model.ExecuteCodeRequest;
import com.yangmao.yangojcodesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ProcessUtils {

    public static ExecuteMessage doProcess(Process process,String opName){

        ExecuteMessage executeMessage = new ExecuteMessage();
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();

            //获取进程的正常输出
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String compileOutputLine;
            StringBuilder compileOutput = new StringBuilder();
            while((compileOutputLine= bufferedReader.readLine()) !=null){
                compileOutput.append(compileOutputLine);
            }
            log.info(opName+"正常输出："+compileOutput);
            executeMessage.setMessage(compileOutput.toString());

            int exitValue = process.waitFor();
            executeMessage.setExitValue(exitValue);
            if(exitValue==0){
                log.info(opName+"成功...");
            }else{
                log.error(opName+"失败...");
                //获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
                String errorCompileOutputLine;
                StringBuilder errorCompileOutput = new StringBuilder();
                while((errorCompileOutputLine=errorBufferedReader.readLine())!=null) {
                    errorCompileOutput.append(errorCompileOutputLine);
                }
                log.info("进程的错误输出："+errorCompileOutput);
                executeMessage.setErrorMessage(errorCompileOutput.toString());
                errorBufferedReader.close();
            }
            bufferedReader.close();
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return executeMessage;
    }

}
