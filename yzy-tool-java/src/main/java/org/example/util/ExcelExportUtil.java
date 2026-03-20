package org.example.util;

/**
 * @description: Excel 导出工具类
 * @author:
 * @date 2024/10/12 15:55
 * @version 1.0
 */
import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ExcelExportUtil {

    /**
     * 将数据导出为 Excel 文件。
     *
     * @param data      表格数据，键为表名，值为表的数据列表。每条数据为列名到字符串值的映射。
     * @return ByteArrayOutputStream 包含 Excel 文件的字节数组输出流。
     * @throws IOException 如果发生 I/O 错误
     */
    public ByteArrayOutputStream exportToExcel( Map<String, List<Map<String, String>>> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建标题样式（第一行）
        CellStyle titleStyle = createTitleStyle(workbook);
        data.size();
        // 创建表头样式（第二行）
        CellStyle headerStyle = createHeaderStyle(workbook);

        // 创建数据单元格样式（居中对齐 + 边框）
        CellStyle dataStyle = createDataStyle(workbook);

        for (Map.Entry<String, List<Map<String, String>>> tableEntry : data.entrySet()) {
            String sheetName = tableEntry.getKey();
            List<Map<String, String>> rows = tableEntry.getValue();

            Sheet sheet = workbook.createSheet(sheetName);
            // 创建标题行（第一行）
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(titleStyle);

            if (null == rows || rows.isEmpty()) {
                titleCell.setCellValue("无数据可导入");
                int firstColumnWidth = 50 * 256; // 设置为50个字符宽
                sheet.setColumnWidth(0, firstColumnWidth);
                continue;
            }else{
                titleCell.setCellValue(sheetName);

            }
            // 获取表头
            Map<String, String> firstRow = rows.get(0);
            String[] headers = firstRow.keySet().toArray(new String[0]);



            if(1 < headers.length) {
                // 合并单元格以覆盖所有列
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.length - 1));
            }
            // 创建表头行（第二行）
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据行（从第三行开始）
            for (int i = 0; i < rows.size(); i++) {
                Map<String, String> rowData = rows.get(i);
                Row row = sheet.createRow(i + 2); // 数据从第三行开始

                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.createCell(j);
                    String cellValue = rowData.getOrDefault(headers[j], "");
                    cell.setCellValue(cellValue);
                    cell.setCellStyle(dataStyle);
                }
            }

            // 自动调整列宽并设置最小宽度
            int minWidth = 15 * 256; // 15 characters
            int maxWidth = 30 * 256; // 30 characters (optional)
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < minWidth) {
                    sheet.setColumnWidth(i, minWidth);
                } else if (sheet.getColumnWidth(i) > maxWidth) { // 可选：设置最大宽度
                    sheet.setColumnWidth(i, maxWidth);
                }
            }
        }

        workbook.write(out);
        workbook.close();

        return out;
    }

    /**
     * 创建标题行样式
     *
     * @param workbook 工作簿
     * @return CellStyle 标题样式
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置背景颜色
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(Boolean.TRUE);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建表头样式
     *
     * @param workbook 工作簿
     * @return CellStyle 表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置背景颜色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(Boolean.TRUE);
        style.setFont(font);
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据单元格样式
     *
     * @param workbook 工作簿
     * @return CellStyle 数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(Boolean.TRUE);
        // 设置边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
