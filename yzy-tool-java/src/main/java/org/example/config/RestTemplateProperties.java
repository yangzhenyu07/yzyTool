package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2022/7/4 14:08
 */
@Configuration
@ConfigurationProperties(prefix = "yzy.file.rest.template", ignoreUnknownFields = false)
public class RestTemplateProperties {
    @Value("10000")
    private int socketConnectTimeout;
    @Value("10000")
    private int connectionRequestTimeout;
    @Value("5000")
    private int connectTimeout;
    @Value("500")
    private int maxTotal;
    @Value("100")
    private int defaultMaxPerRoute;
    @Value("3")
    private int requestRetryCount;

    public RestTemplateProperties() {
    }

    public int getSocketConnectTimeout() {
        return this.socketConnectTimeout;
    }

    public void setSocketConnectTimeout(int socketConnectTimeout) {
        this.socketConnectTimeout = socketConnectTimeout;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return this.connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getDefaultMaxPerRoute() {
        return this.defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getRequestRetryCount() {
        return this.requestRetryCount;
    }

    public void setRequestRetryCount(int requestRetryCount) {
        this.requestRetryCount = requestRetryCount;
    }
}
