package com.wangdazhi.wangaiagent.app;


import com.wangdazhi.wangaiagent.Advisor.MyLoggerAdvisor;
import com.wangdazhi.wangaiagent.Advisor.ProhibitedWordAdvisor;
import com.wangdazhi.wangaiagent.chatmemory.FileBasedChatMemory;
import com.wangdazhi.wangaiagent.rag.PgVectorVectorStoreConfig;
import com.wangdazhi.wangaiagent.rag.QueryRewriter;
import com.wangdazhi.wangaiagent.rag.StudyAppRagCustomAdvisorFactory;
import com.wangdazhi.wangaiagent.tools.TerminalOperationTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@Component
@Slf4j
public class StudyApp {


   private ChatClient chatClient;

   private static final String SYSTEM_PROMPT="扮演深耕学习规划与学习心理领域的专家，开场向用户表明身份，告知用户可倾诉学习难题，" +
           "围绕三个核心方面提问：学习类型识别——询问考试类型、目标院校/岗位及考试时间；" +
           "学习能力要求分析——考试科目、能力要求、基础评估及学习资源准备；" +
           "学习规划指导——时间规划、阶段性安排、每日安排、复习策略及压力管理，引导用户详述具体学习目标、当前状态、尝试方法、时间困扰及心理状态，" +
           "以便给出个性化的学习计划制定、针对性方法建议、时间管理策略、心理调适方法及阶段性目标设定等专属解决方案。";
   public StudyApp(ChatModel dashscopeChatModel){
       //基于文件FileBasedChatMemory的聊天记忆
//       String fileDir=System.getProperty("user.dir")+"/tmp/chat-memory";
//       FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);

       //InMemoryChatMemory基于内存的聊天记忆
       InMemoryChatMemory chatMemory = new InMemoryChatMemory();
       chatClient = ChatClient.builder(dashscopeChatModel)
               .defaultSystem(SYSTEM_PROMPT)
               .defaultAdvisors(
                       new MessageChatMemoryAdvisor(chatMemory),
                       new ProhibitedWordAdvisor()
               )
               .build();
   }
   /**
    *AI基础对话，支持多轮对话记忆
    *
    *
    * */
   public String dochat(String message,String chatId){
       ChatResponse chatResponse = chatClient.prompt()
               .user(message)
               .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                       .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
               .call()
               .chatResponse();
       String content = chatResponse.getResult().getOutput().getText();
      log.info("content:{} ",content);

       return content;
   }

    /**
     *AI基础对话，支持多轮对话记忆
     *
     *
     * */
    public Flux<String> dochatByStream(String message,String chatId){
        Flux<String> result = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();


        return result;
    }


    record StudyReport(String title, String stage,List<String> suggestions) {

    }
    /**
     *AI恋爱报告功能，演示结构化输出
     *定义一个结构体，包含三个字段，标题、阶段、建议列表
     * 然后再原有基础上entity
     *
     * */
    public StudyReport dochatWithReport(String message,String chatId){
        StudyReport studyReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成学习规划结果，标题为{用户名}的学习计划书，内容为各个阶段以及各个阶段的建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(StudyReport.class);
        log.info("studyReport:{} ", studyReport);
        return studyReport;
    }
    /*
    * 恋爱知识库问答功能
    *
    */
    @Resource
    private VectorStore studyAppVectorStore;

    @Resource
    private Advisor StudyAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;



    public String doChatWithRag(String message, String chatId) {
        String rewrite = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrite)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(
                        new MyLoggerAdvisor(),
                        //本地知识库问答
                        new QuestionAnswerAdvisor(studyAppVectorStore),
                       StudyAppRagCustomAdvisorFactory.createStudyAppRagCustomAdvisor(studyAppVectorStore, "事业单位")
                         //启用云知识库服务
//                           StudyAppRagCloudAdvisor
                        /* 启用基于Pgvector的云知识库服务
                           new QuestionAnswerAdvisor(pgVectorVectorStore)*/
                            )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    /**
     * AI知识库调用工具的功能
     */

    @Resource
    private ToolCallback[] allTools;
    public String dochatWithTools(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{} ",content);

        return content;
    }

    /**
     * AI调用mcp服务
     */

    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    public String dochatWithMcp(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{} ",content);

        return content;
    }



}
