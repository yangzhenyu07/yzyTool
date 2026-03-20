package org.example.ssl.config;


import org.apache.catalina.connector.Connector;
import org.example.ssl.common.BaseCondition;
import org.example.ssl.common.MySslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangzhenyu
 * @date 2024/7/12 9:29
 * @version 1.0
 */
@Configuration
@Conditional(MySslConfig.class)
public class AddPortConfig implements BaseCondition {

    @Value("${server.ssl.addHttpPort:8080}")
    private String addPort;

    private static Logger log = LoggerFactory.getLogger(AddPortConfig.class);
    public AddPortConfig(){
        log.info("=================== add bean AddPortConfig ===================");
    }
    @Bean
    @Conditional(MySslConfig.class)
    public ServletWebServerFactory servletContainer() throws Exception {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        // HTTP 端口
        factory.addAdditionalTomcatConnectors(createHttpConnector(trim(addPort)));
        return factory;

    }
 

    public String trim(String msg) {
        return msg.trim();
    }

    private Connector createHttpConnector(String port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(Integer.parseInt(port));

        return connector;
    }

}
