package org.example.config;


import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2022/7/4 14:08
 */
@Configuration
public class RestTemplateConfig {
    @Autowired
    private RestTemplateProperties properties;

    public RestTemplateConfig() {
    }

    @Bean(name = "poolingHttp")
    public HttpClientConnectionManager poolingHttp() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(this.properties.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(this.properties.getDefaultMaxPerRoute());
        return connectionManager;
    }

    @Bean(name = "client")
    public HttpClient client(@Qualifier("poolingHttp")
                                 HttpClientConnectionManager connectionManager) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(connectionManager);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(this.properties.getRequestRetryCount(), true));
        return httpClientBuilder.build();
    }

    @Bean(name = "clientFactory")
    public ClientHttpRequestFactory clientFactory(@Qualifier("client")
                                                             HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        clientHttpRequestFactory.setConnectionRequestTimeout(this.properties.getConnectionRequestTimeout());
        clientHttpRequestFactory.setConnectTimeout(this.properties.getConnectTimeout());
        clientHttpRequestFactory.setReadTimeout(this.properties.getSocketConnectTimeout());
        return clientHttpRequestFactory;
    }

    @Bean({"yzyTemplate"})
    public RestTemplate yzyTemplate(@Qualifier("clientFactory")
                                       ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        return restTemplate;
    }





}