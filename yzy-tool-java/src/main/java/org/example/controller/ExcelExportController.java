package org.example.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.annotation.CommonLog;
import org.example.service.ExcelExportService;
import org.example.util.ExcelExportRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/10/12 16:00
* @version 1.0
*/
@Api(value = "excel导出", tags = {" excel导出"})
@RestController
@Slf4j
@RequestMapping(value="api/excel")
public class ExcelExportController {

    @Resource
    ExcelExportService excelExportService;


    /**
     * 导出 Excel 文件的端点。
     * {
     * 	"data": {
     * 		"测试Test-01": [
     *            {"姓名":"杨振宇","年龄":"30","班级":"一班","额度":"99765757587564564644"},
     *            {"姓名":"哈哈","年龄":"23","班级":"一班","额度":"45.564"},
     *            {"姓名":"你好","年龄":"12","班级":"二班","额度":"999.9999"},
     *            {"姓名":"哈喽","年龄":"78","班级":"三班","额度":"-89.4"},
     *            {"姓名":"xxxx","年龄":"45","班级":"一班","额度":"10101010"}
     *
     * 		],"测试Test-02": [
     *            {"姓名":"CCC","年龄":"90","班级":"三班","额度":"99765757587564564644"},
     *            {"姓名":"AAA","年龄":"66","班级":"一班","额度":"45.564"},
     *            {"姓名":"44FF","年龄":"22","班级":"二班","额度":"999.9999"},
     *            {"姓名":"VVV","年龄":"23","班级":"三班","额度":"-89.4"},
     *            {"姓名":"CCX","年龄":"44","班级":"一班","额度":"10101010"}
     *
     * 		]
     * 	},
     * 	"directory": "测试excel"
     * }
     * @param request Excel 导出请求，包含目录和数据
     * @return Excel 文件下载响应
     */
    @ApiOperation(value = "excl 导出", notes = "excl 导出")
    @CommonLog(methodName = "excl 导出",className = "ExcelExportController#export",url = "api/excel/export")
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> export(@RequestBody ExcelExportRequest request) {
        String directory = request.getDirectory();
        if (directory == null || directory.trim().isEmpty()) {
            directory = "exported_data"; // 默认文件名
        }

        try {
            ByteArrayOutputStream out = excelExportService.export(directory, request.getData());
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            // 设置文件名，确保文件名中不含有非法字符
            String fileName = URLEncoder.encode(directory + ".xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(bytes.length));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(bytes.length)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            log.error("错误",e);
            return ResponseEntity.status(500).build();
        }
    }
    @CommonLog(methodName = "测试-excl 导出",className = "ExcelExportController#getTest",url = "api/excel/getTest")
    @GetMapping("/getTest")
    public void getTest(HttpServletResponse response) {
        String directory = "exported_data";
        Map<String, List<Map<String, String>>> data = Maps.newHashMap();
        data.put("测试", Lists.newArrayList());
        try {
            ByteArrayOutputStream out = excelExportService.export(directory, data);
            byte[] bytes = out.toByteArray();
            // 设置文件名，确保文件名中不含非法字符
            String fileName = URLEncoder.encode(directory + ".xlsx", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Length", String.valueOf(bytes.length));

            // 写入输出流
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("错误", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
