package com.wangdazhi.wangaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WangManusTest {

    @Resource
    private WangManus wangManus;
    @Test
    public void testRun() {
        String message = """
                            我的学习地点在南京雨花台万象天地，
                            请帮我找到5km内的学习合适的地点并结合一些网络图片，
                            同时也制定一份详细的周一-周日的每天学习计划""";
        String result = wangManus.run(message);
        Assertions.assertNotNull(result);
    }

}