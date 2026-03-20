package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.ExcelExportService;
import org.example.util.ExcelExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    private final ExcelExportUtil excelExportUtil;

    @Autowired
    public ExcelExportServiceImpl(ExcelExportUtil excelExportUtil) {
        this.excelExportUtil = excelExportUtil;
    }

    /**
     * 导出数据为 Excel。
     *
     * @param directory 目录名称
     * @param data      表格数据
     * @return ByteArrayOutputStream 包含 Excel 文件的字节数组输出流
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public ByteArrayOutputStream export(String directory, Map<String, List<Map<String, String>>> data) throws IOException {
        return excelExportUtil.exportToExcel(data);
    }
}
