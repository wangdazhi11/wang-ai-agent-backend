package com.wangdazhi.wangaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudyAppTest {

    @Resource
    private StudyApp studyApp;

    @Test
    void Testchat() {
        String ChatID = UUID.randomUUID().toString();
        //第一轮

        String answer = studyApp.dochat("你好,我叫王大志",ChatID);
        // 第二轮
        answer = studyApp.dochat("我想问河海大学的研究生关于电子信息基本上有哪几科目，具体考哪些",ChatID);
        Assertions.assertNotNull(answer);
        // 第三轮
        answer = studyApp.dochat("这些科目有哪些考研名师",ChatID);
        Assertions.assertNotNull( answer);
    }

    @Test
    void Testchat1() {
        String ChatID = UUID.randomUUID().toString();
        //第一轮

        String answer = studyApp.dochat("fuck",ChatID);
    }



    @Test
    void dochatWithReport() {
        String ChatID = UUID.randomUUID().toString();
        StudyApp.StudyReport studyReport  = studyApp.dochatWithReport("你好,我叫王大志,我想问考研电子信息，怎么做", ChatID);
        Assertions.assertNotNull( studyReport);
    }

    @Test
    void doChatWithRag() {
        String ChatID = UUID.randomUUID().toString();
        //第一轮

        String answer = studyApp.doChatWithRag("新手应从哪一步入手准备事业编考试？",ChatID);

        Assertions.assertNotNull(answer);

    }


    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想去南京建邺区附近的自习室学习，推荐几个适合学习的地方？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近学习不太顺利，看看党政的学习网站（https://www.xuexi.cn/），是怎么解决问题的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的学习图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的学习档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘周末学习计划’PDF，包含学习时间、学习地点和学习计划");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = studyApp.dochatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void dochatWithMcp() {

        String ChatID = UUID.randomUUID().toString();
//        String answer = studyApp.dochatWithMcp("我想问雨花台万象天地附近附近的自习室有哪些",ChatID);
//        Assertions.assertNotNull(answer);
        /**
         *图片搜索
         */
        String answer = studyApp.dochatWithMcp("我想搜索一些图书馆的照片",ChatID);
        Assertions.assertNotNull(answer);
    }

    @Test
    void dochatByStream() {
    }
}