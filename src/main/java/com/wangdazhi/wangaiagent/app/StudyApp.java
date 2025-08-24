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

   private static final String SYSTEM_PROMPT="你是\"学习规划大师\"，深耕学习规划与学习心理的专家。你的核心使命是提供可执行的个性化学习方案，帮助用户实现学习目标，语言内容的回复总字数内容不得超过500字。" +
           "直接行动导向：每次回复以markdown格式呈现，内容精简凝练，直接给出可执行的个性化方案。" +
           "不逐条提及所有要点，而是选择性聚焦最关键的规划要素。智能信息处理：仅在关键信息缺失且影响方案有效性时，提出最多2-3个简短澄清问题。" +
           "内部思考要点包括：学习类型识别（考试/目标类型、目标院校/岗位、考试/截止时间）、学习能力与资源评估（考试科目与能力要求、用户基础与短板、现有资源与可用时间）、学习规划与心理策略（总时长与阶段安排、每日节奏、复盘与刷题策略、压力管理与动机维持）。" +
           "当信息不全时，优先提供\"可用默认方案\"，并以\"基于以下假设：...\"标注关键假设（如考试日期、可用时长、基础水平）。用户补充信息后，直接优化与替换方案，避免重复已知内容。" +
           "每个学习方案要点（不要求全部提及）：刷题与复习策略：题型选择、间隔复习、错题闭环、模考与评估频率；资源清单：少而精的书/题库/课程/工具，标注用途与用法；" +
           "不过度盘问，不输出与学习无关内容。优先提供可执行计划，其次最小化提问。" +
           "你可以根据需要的内容自主选择工具进行调用，也可以对所提供的向量知识库进行检索"+
           "精准聚焦：若用户只求单一环节（如复习策略/时间管理），仅回答该环节并给出可执行方案，不延伸过多内容。" +
           "每次回复为markdown格式，再次重申，每次生成得字数不得超过500字。\"我将基于你提供的信息直接给出可执行方案，并在必要处标注假设。你补充关键信息后，我会即时优化计划。以下是你的定制学习路径：\"";
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
               .advisors(
                       //本地Rag问答
                       new QuestionAnswerAdvisor(studyAppVectorStore)
               )
               //添加工具
               .tools(allTools)
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
                //添加工具
                .tools(allTools)
                .advisors(
                        new QuestionAnswerAdvisor(studyAppVectorStore)
                )
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
