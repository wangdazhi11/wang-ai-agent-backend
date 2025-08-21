package com.wangdazhi.wangaiagent.rag;



import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 *
 * 学习规划大师的向量数据库配置（基于内存的向量数据库bean）
 */
@Configuration
public class StudyAppVectorStoreConfig {
    @Resource
    private StudyAppDocumentLoader studyAppDocumentLoader;
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;




    @Bean
    VectorStore studyAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documents = studyAppDocumentLoader.loadMarkdown();
//        List<Document> splitdocuments1 = myTokenTextSplitter.splitCustomized(documents);
        List<Document> documents1 = myKeywordEnricher.enrichDocuments(documents);
        simpleVectorStore.add(documents1);
        return simpleVectorStore;
    }
}