package com.wangdazhi.wangaiagent.rag;

import com.alibaba.cloud.ai.document.TextDocumentParser;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.ml.model.PlainTextFileDataReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 开发学习规划应用文档加载器
 */


@Component
@Slf4j
class StudyAppDocumentLoader {

    private  final ResourcePatternResolver resourcePatternResolver;

    private StudyAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdown() {
        List<Document> documents = new ArrayList<>();

//        加载多篇markdown
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");

            for (Resource resource : resources){
                String filename = resource.getFilename();
                String status = filename.substring(filename.length() - 8, filename.length() - 4);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status",status)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                documents.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 加载文档失败", e);
        }
        return documents;
    }

}
