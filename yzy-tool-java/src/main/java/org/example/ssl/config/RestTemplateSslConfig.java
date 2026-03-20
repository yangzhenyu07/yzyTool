package org.example.ssl.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.example.ssl.common.BaseCondition;
import org.example.ssl.common.EnableCheckBean;
import org.example.ssl.common.MyConditional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author yangzhenyu
 * @date 2024/7/12 9:29
 * @version 1.0
 */
@Configuration
@Conditional(MyConditional.class)
public class RestTemplateSslConfig {

    @Value("${server.ssl.key-store}")
    private String keyStorePath;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${server.ssl.trust-store}")
    private String trustStorePath;

    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;
    private final ResourceLoader resourceLoader;
    private static Logger log = LoggerFactory.getLogger(RestTemplateSslConfig.class);

    public RestTemplateSslConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Primary
    @Bean
    @Conditional(MyConditional.class)
    @EnableCheckBean(name = BaseCondition.REST_TEMPLATE_NAME,load = BaseCondition.REST_TEMPLATE_KEY)
    public RestTemplate restTemplate() throws Exception {
        log.info("=================== add bean RestTemplateSslConfig restTemplate===================");

        Resource keyStoreResource = resourceLoader.getResource(keyStorePath);
        Resource trustStoreResource = resourceLoader.getResource(trustStorePath);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keyStoreInputStream = keyStoreResource.getInputStream()) {
            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
        }

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream trustStoreInputStream = trustStoreResource.getInputStream()) {
            trustStore.load(trustStoreInputStream, trustStorePassword.toCharArray());
        }

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                .loadTrustMaterial(trustStore, null)
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
