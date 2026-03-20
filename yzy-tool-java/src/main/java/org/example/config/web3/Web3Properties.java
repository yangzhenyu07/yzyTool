package org.example.config.web3;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Web3配置属性类：绑定application.yml中的web3配置
 * @author 杨镇宇
 * @date 2026/3/19 22:33
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "web3")
public class Web3Properties {
    // 多链多节点配置（key: 节点标识，value: Web3NodeVO）
    private Map<String, List<Web3NodeVO>> nodes;
    // 通用配置
    private Config config;

    // 节点配置-白名单
    private String nodeNames;

    @Data
    public static class Config {
        private long  connectTimeout = 5000;
        private long  readTimeout = 10000;
    }
    @Data
    public static class Web3NodeVO {

        private String name;
        private String url;
        private Integer weight;
    }
}