package com.wangdazhi.wangaiagent.tools;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.wangdazhi.wangaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（实现文件读写操作）
 */

public class FileOperationTool {
    private static final String FILE_PATH= FileConstant.FILE_SAVE_DIR + "/file";


    @Tool(description = "读取文件内容")
    public String readFile(@ToolParam(description = "文件名")String filename) {
        String filePath = FILE_PATH + "/" + filename;
        try {
            String s = FileUtil.readUtf8String(filePath);
            return "读取成功\n"+s;
        } catch (Exception e) {
            return "文件不存在"+e.getMessage();
        }

    }

    @Tool(description = "写入文件内容")
    public String writeFile(@ToolParam(description = "要写入的文件名")String filename,
                            @ToolParam(description = "要写入文件内容") String content) {
        String filePath = FILE_PATH + "/" + filename;
        try {
            FileUtil.mkdir(FILE_PATH);
            FileUtil.writeUtf8String(content,filePath);
            return "写入成功"+filePath;
        } catch (Exception e) {
            return "写入失败"+e.getMessage();
        }
    }

}
