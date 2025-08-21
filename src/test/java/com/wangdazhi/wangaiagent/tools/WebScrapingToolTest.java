package com.wangdazhi.wangaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {

        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String s = webScrapingTool.scrapeWebPage("https://www.codefather.cn");
        Assertions.assertNotNull(s);

    }
}
