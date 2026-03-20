package org.example.startupdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yangzhneyu
 * 项目启动预处理1--添加关闭钩子
 * 作用:springBoot 项目启动预先加载数据
 * 通过@Order来排序，数字越小，越先执行
 * */
@Component
@Order(1)
public class StartUpRunner implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(StartUpRunner.class);


    @Override
    public void run(String... args) throws Exception {
        log.info("===================项目启动预处理--添加关闭钩子===================");
        //添加关闭挂钩 ShutdownHook允许开发人员在JVM关闭时执行相关的代码
        /**
         * 1.程序正常退出 ， JVM关闭
         * 2. 调用System.exit ，JVM关闭
         * 3. 程序抛出异常，导致JVM关闭
         * 4. OOM 导致JVM关闭
         * 5. 外界：Ctrl + C ，导致JVM关闭
         * 6. 外界：用户注销或者关机，导致JVM关闭
         * 7. 外界：kill 信号 （kill -9 除外）
         * */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("等待30秒，延迟停止...");
                    Thread.sleep(30000); // 延迟30秒
                } catch (InterruptedException e) {
                    log.error("延迟停止过程中出现错误", e);
                }
            }
        }));
    }
}
