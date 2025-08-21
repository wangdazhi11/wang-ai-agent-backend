package com.wangdazhi.wangaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String s = fileOperationTool.readFile("test.txt");
        System.out.println(s);
        Assertions.assertNotNull(s);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String s = fileOperationTool.writeFile("test.txt","你好");
        Assertions.assertNotNull(s);
    }
}