package org.example.ssl.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
* @author yangzhenyu
* @date 2024/7/12 9:34
* @version 1.0
*/

public class MySslConfig implements Condition,BaseCondition{
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String flag = environment.getProperty(SSL_CONFIG_KEY);
        if(StringUtils.equals(TAG,flag)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
