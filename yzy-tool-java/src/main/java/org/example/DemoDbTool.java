package org.example;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2023/4/26 9:46
 */
@EnableAsync
@SpringBootApplication(scanBasePackages={"org.example"},exclude = R2dbcAutoConfiguration.class)
public class DemoDbTool {
    public static void main(String[] args) {
        SpringApplication.run(DemoDbTool.class, args);
    }


}
