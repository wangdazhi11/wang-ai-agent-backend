package com.wangdazhi.wangaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring Ai Ai Invoke
 * @author wangdazhi
 * @date 2023-07-09
**/
//@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {

        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，请回答我狗怎么叫"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());



    }
}
