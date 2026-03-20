package org.example.util;
/**
* @description: TODO
* @author 杨镇宇
* @date 2024/10/12 15:57
* @version 1.0
*/

import java.util.List;
import java.util.Map;

public class ExcelExportRequest {

    /**
     * 目录名称，可以用作 Excel 文件的名称或分类。
     */
    private String directory;

    /**
     * 数据内容，键为表名，值为表的数据列表。
     * 每条数据为列名到字符串值的映射。
     */
    private Map<String, List<Map<String, String>>> data;

    // Getters and Setters

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Map<String, List<Map<String, String>>> getData() {
        return data;
    }

    public void setData(Map<String, List<Map<String, String>>> data) {
        this.data = data;
    }
}
