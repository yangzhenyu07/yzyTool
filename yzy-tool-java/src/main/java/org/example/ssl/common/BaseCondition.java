package org.example.ssl.common;

/**
* @author yangzhenyu
* @date 2024/7/12 9:28
* @version 1.0
*/

public interface BaseCondition {
    String TAG = "PSBC@12!5440_YES";
    String BEAN_KEY = "https.config.flag";
    String SSL_CONFIG_KEY = "https.appPort-config.flag";


    String REST_TEMPLATE_KEY = "https.restTemplate.flag";
    String REST_TEMPLATE_NAME = "restTemplateClient";

    String MSG_CHECK = "restTemplateClient";
    String ENABLE_CHECK_BEAN_NAME = "name";
    String ENABLE_CHECK_BEAN_LOAD = "load";
}
