package com.wangdazhi.wangaiagent.controller;


import com.wangdazhi.wangaiagent.agent.BaseAgent;
import com.wangdazhi.wangaiagent.agent.WangManus;
import com.wangdazhi.wangaiagent.app.StudyApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private StudyApp studyApp;
    @Resource
    private WangManus wangManus;


    // 同步调用
    @GetMapping("/studyApp/chat/sync")
    public String studyAppChatSync(String message,String chatId)
    {
        return studyApp.dochat(message,chatId);
    }

    // SSE流式输出接口
    @GetMapping(value = "/studyApp/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> studyAppChatSSE(String message, String chatId)
    {
        return studyApp.dochatByStream(message,chatId);
    }

    //使用serversentevent来包装，这样可以省略MediaType.TEXT_EVENT_STREAM_VALUE
    @GetMapping("/studyApp/chat/ServerSentEvent")
    public Flux<ServerSentEvent<String>> studyAppChatServerSentEvent(String message, String chatId)
    {
        return studyApp.dochatByStream(message,chatId)
                .map(content -> ServerSentEvent.<String>builder().data( content).build());
    }

    //使用SSEEmiter,实现流式输出
    @GetMapping("/studyApp/chat/SSEEmiter")
    public SseEmitter studyAppChatSSEEmiter(String message, String chatId)
    {

        //首先获得对象sseemitter
        SseEmitter sseEmiter = new SseEmitter(300000L);
        studyApp.dochatByStream(message,chatId)
                .subscribe(chunk->{
                    try {
                    sseEmiter.send(chunk);
                }catch (Exception e){
                    sseEmiter.completeWithError(e);
                      }
                },
                sseEmiter::completeWithError, sseEmiter::complete
                );
        return sseEmiter;
    }

    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/manus/chat")
    public SseEmitter manusChat(String message)
    {
        WangManus wangManus = new WangManus(allTools,dashscopeChatModel);
        return wangManus.runStream(message);
    }


}
