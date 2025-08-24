package com.wangdazhi.wangaiagent.agent;

/**
 * 抽象基础代理类，管理代理状态以及执行流程
 * 状态转换、内存管理和基于步骤的执行循环的基础功能
 * 子类实现step方法
 */


import cn.hutool.core.util.StrUtil;
import com.wangdazhi.wangaiagent.agent.model.AgentState;
import io.reactivex.Completable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {
    //核心属性
    private String name;

    //提示词
    private String systemPrompt;
    private String nextPrompt;

    //代理状态
    private AgentState state = AgentState.IDLE;

    //循环步骤
    private int currentStep =0;
    private int maxSteps = 5;

    //LLM大模型
    private ChatClient chatClient;




    //上下文记忆(自主维护会话上下文)
    private List<Message> messageList = new ArrayList<>();

    //运行代理，定义流程
    public String run(String userPrompt) {
        //1.1状态检查,前提得是空闲中,
        if(this.state!=AgentState.IDLE){
            throw new RuntimeException("状态为"+this.state+"，状态异常。无法执行智能体。");
        }
        //1.2输入检查
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("用户输入为空,状态异常。无法执行智能体。");
        }
        //2.状态转换
        this.state = AgentState.RUNNING;

        //加入上下文
        messageList.add(new UserMessage(userPrompt));

        //保存结果
        List<String> Results = new ArrayList<>();


        try {
            //2.循环执行步骤
            while (currentStep < maxSteps && state != AgentState.FINISHED) {
                currentStep++;
                log.info("当前步骤：{}/{}", currentStep,maxSteps);
                String stepResult = step();
                String Result = "步骤"+currentStep+": " + stepResult;
                Results.add(Result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                this.state = AgentState.FINISHED;
                log.info("步骤超出限制，已结束");
                Results.add("终止：已达到最大步骤"+"("+maxSteps+")");
            }
            return String.join("\n",Results);

        } catch (Exception e) {
            this.state = AgentState.ERROR;
            log.error("执行异常：{}", e.getMessage());
            return "执行错误：" + e.getMessage();
        }finally {
            //3.清理资源
            this.cleanup();
        }
    }

    //运行代理，定义流程
    public SseEmitter runStream(String userPrompt) {
        SseEmitter Sseemitter = new SseEmitter(300000L);//默认超时时间5分钟
       //整个调用过程运行在异步线程中，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                //1.1状态检查,前提得是空闲中,

                if(this.state!=AgentState.IDLE){
                        Sseemitter.send("状态为"+this.state+"，状态异常。无法执行智能体。");
                        Sseemitter.complete();
                    return;
                }
                //1.2输入检查
                if(StrUtil.isBlank(userPrompt)){
                        Sseemitter.send("空提示词异常。无法执行智能体。");
                        Sseemitter.complete();
                         return;
                }

                //2.状态转换
                this.state = AgentState.RUNNING;

                //加入上下文
                messageList.add(new UserMessage(userPrompt));


                try {
                    //2.循环执行步骤
                    while (currentStep < maxSteps && state != AgentState.FINISHED) {
                        currentStep++;
                        log.info("当前步骤：{}/{}", currentStep,maxSteps);
                        String stepResult = step();
                        String Result = "步骤"+currentStep+": " + stepResult;
                        Sseemitter.send(Result);
                    }
                    //检查是否超出步骤限制
                    if (currentStep >= maxSteps) {
                        this.state = AgentState.FINISHED;
                        log.info("步骤超出限制，已结束");
                        Sseemitter.send("终止：已达到最大步骤"+"("+maxSteps+")");
                    }
                    Sseemitter.complete();
                } catch (Exception e) {
                    this.state = AgentState.ERROR;
                    log.error("执行异常：{}", e.getMessage());
                    try {
                        Sseemitter.send("执行错误：" + e.getMessage());
                        Sseemitter.complete();
                    } catch (IOException ex) {
                        Sseemitter.completeWithError(ex);
                    }
                }finally {
                    //3.清理资源
                    this.cleanup();
                }
            } catch (IOException e) {
                Sseemitter.completeWithError(e);
            }

        });

        Sseemitter.onTimeout(()->{
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE连接超时");
        });

        Sseemitter.onCompletion(() -> {
            if(this.state==AgentState.RUNNING) this.state = AgentState.FINISHED;
            this.cleanup();
            log.info("SSE连接已完成");
        });
        return Sseemitter;
    }


    //定义单个步骤，用于子类实现
     public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        //TODO:清理资源

    }
}
