package org.example.controller;

import org.example.annotation.CommonLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/10/12 16:36
* @version 1.0
 *
 * 一个 @Controller 用于返回 Thymeleaf 视图。
 * 一个 @RestController 用于处理导出 Excel 的 REST API。
*/
@Controller
@RequestMapping("/api/view")
public class ExcelExportPageController {
    @CommonLog(methodName = "excl 导出页面",className = "ExcelExportPageController#exportPage",url = "api/view/export-page")
    @GetMapping("/export-page")
    public String exportPage() {
        return "export"; // 返回 src/main/resources/templates/export.html
    }
}
