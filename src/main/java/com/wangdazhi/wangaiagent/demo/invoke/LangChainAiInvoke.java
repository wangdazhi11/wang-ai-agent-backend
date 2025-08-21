package com.wangdazhi.wangaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChainAiInvoke {
    public static void main(String[] args) {
        String chat = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build()
                .chat("请将下面这段文字翻译成英文：\n" +
                        "我叫王大志，现在正在学习LangChain。");
        System.out.println(chat);
    }
}
