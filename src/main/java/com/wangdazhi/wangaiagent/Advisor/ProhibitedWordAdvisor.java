package com.wangdazhi.wangaiagent.Advisor;


import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/*
* 对用户的输入进行过滤
* 敏感词过滤
* 导入依赖
* <dependency>
    <groupId>com.github.houbb</groupId>
    <artifactId>sensitive-word</artifactId>
    <version>0.27.1</version>
</dependency>
*
*
* */



@Slf4j
public class ProhibitedWordAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    public String getName() {
        return this.getClass().getSimpleName();
    }
    public int getOrder() {
        return -100;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        String userText = request.userText();
        if (SensitiveWordHelper.contains(userText)) {
            log.info("用户输入的文本包含违禁词：{}", SensitiveWordHelper.findAll(userText));
            throw new IllegalArgumentException("用户输入的文本包含违禁词：" + SensitiveWordHelper.findAll(userText));
        }
        return request;
    }


    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);

        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

        return advisedResponse;
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return advisedResponses;
    }
}

