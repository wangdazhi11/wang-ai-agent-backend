package com.wangdazhi.wangaiagent;


import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class WangAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangAiAgentApplication.class, args);
    }


}
