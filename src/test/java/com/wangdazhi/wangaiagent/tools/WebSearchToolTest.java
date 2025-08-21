package com.wangdazhi.wangaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSearchToolTest {
    @Value("${search-api.api-key}")
    private String apiKey;
    @Test
    public void testSearchWeb() {

        WebSearchTool tool = new WebSearchTool(apiKey);
        String query = "西安长亮商贸有限公司";
        String result = tool.searchWeb(query);
        Assertions.assertNotNull(result);
    }
}
