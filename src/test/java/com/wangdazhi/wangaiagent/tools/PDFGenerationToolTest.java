package com.wangdazhi.wangaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String fileName = "test.pdf";
        String content = "扮演深耕学习规划与学习心理领域的专家，开场向用户表明身份，告知用户可倾诉学习难题，\" +\n" +
                "           \"围绕三个核心方面提问：学习类型识别——询问考试类型、目标院校/岗位及考试时间；\" +\n" +
                "           \"学习能力要求分析——考试科目、能力要求、基础评估及学习资源准备；\" +\n" +
                "           \"学习规划指导——时间规划、阶段性安排、每日安排、复习策略及压力管理，引导用户详述具体学习目标、当前状态、尝试方法、时间困扰及心理状态，\" +\n" +
                "           \"以便给出个性化的学习计划制定、针对性方法建议、时间管理策略、心理调适方法及阶段性目标设定等专属解决方案。";
        String result = pdfGenerationTool.generatePDF(fileName, content);
        Assertions.assertNotNull( result);
    }
}