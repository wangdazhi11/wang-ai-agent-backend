package com.wangdazhi.wangaiagent.agent;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.wangdazhi.wangaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 工具调用的基础代理类
 * @author wangdazhi
 * @date 2023-09-05
 */


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {


    // 可用的工具列表
    private ToolCallback[] availableTools;

    //保存think之后需要调用的工具
    private ChatResponse toolCallResponse;

    //工具调用管理者
    private final ToolCallingManager toolCallingManager;

    //禁用springAI内置的工具调用机制，使用自定义的
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();

        ////禁用springAI内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                                              .withProxyToolCalls(true)
                                              .build();
    }

    /**
     * 处理当前状态决定工具调用
     *
     * @return 是否决定调用工具
     */


    @Override
    public boolean think() {
        //校验,校验提示词，拼接用户提示词
        if(getNextPrompt()!=null&&!getNextPrompt().isEmpty()){
            UserMessage userMessage = new UserMessage(getNextPrompt());
            getMessageList().add(userMessage);
        }
List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        //调用AI大模型，获取结果

        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            this.toolCallResponse=chatResponse;
            //解析工具调用结果，获取调用工具
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String result = assistantMessage.getText();

            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName()+"的思考："+ result);
            log.info(getName()+"选择了："+toolCallList.size()+"个工具");
            String toolcalInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具：%s,参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolcalInfo);
//        如果不需要调用工具，返回false
            if (toolCallList.isEmpty()){
                //记录助手消息,不调用工具，需要手动记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            log.error("调用大模型异常",e.getMessage());
            getMessageList().add(new AssistantMessage("大模型出现错误"+e.getMessage()));
            return false;
        }
        //异常处理



    }

    @Override
    public String act() {

        if(!toolCallResponse.hasToolCalls()) return "没有工具调用";

        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        //记录消息上下文,conversationHistory()已经包含了助手消息以及工具调用结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage last = (ToolResponseMessage)CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = last.getResponses().stream().map(response -> "工具" + response.name() + "的返回结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        boolean terminate = last.getResponses().stream().anyMatch(response -> response.name().equals("doTerminate"));
       if(terminate){
           setState(AgentState.FINISHED);
       }
        log.info(results);
        return results;
    }
}
