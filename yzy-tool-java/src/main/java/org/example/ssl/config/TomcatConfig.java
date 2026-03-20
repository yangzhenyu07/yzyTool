package org.example.ssl.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.example.ssl.common.MyConditional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangzhenyu
 * @date 2024/7/12 9:29
 * @version 1.0
 */
@Configuration
@Conditional(MyConditional.class)
public class TomcatConfig {
    private static Logger log = LoggerFactory.getLogger(TomcatConfig.class);

    public TomcatConfig(){
        log.info("=================== add bean TomcatConfig ===================");
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return (factory) -> factory.addContextCustomizers(
                (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customTomcatConfiguration() {
        return factory -> factory.addConnectorCustomizers((Connector connector) -> {
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>) {
                AbstractHttp11Protocol<?> protocolHandler = (AbstractHttp11Protocol<?>) connector.getProtocolHandler();
                // 配置SSL设置，例如设置证书验证选项
                // 这里可以根据需要添加自定义的验证逻辑
            }
        });
    }


}
