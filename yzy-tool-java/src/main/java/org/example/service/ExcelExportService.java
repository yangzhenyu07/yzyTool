package org.example.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/10/12 15:58
* @version 1.0
*/

public interface ExcelExportService {
    ByteArrayOutputStream export(String directory, Map<String, List<Map<String, String>>> data) throws IOException;
}
