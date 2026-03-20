package org.example.ssl.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
* @author yangzhenyu
* @date 2024/7/12 9:29
* @version 1.0
*/
public class MyConditional implements Condition,BaseCondition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String flag = environment.getProperty(BEAN_KEY);
        if(StringUtils.equals(TAG,flag)){
            if (check(environment,annotatedTypeMetadata)){
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean check(Environment environment, AnnotatedTypeMetadata annotatedTypeMetadata){
        boolean hasAnnotation = annotatedTypeMetadata.isAnnotated(EnableCheckBean.class.getName());
        if (hasAnnotation){
            Map<String, Object> attributes = annotatedTypeMetadata.getAnnotationAttributes(EnableCheckBean.class.getName());

            String checkName = (String) attributes.get(ENABLE_CHECK_BEAN_NAME);
            String checkLoad = (String) attributes.get(ENABLE_CHECK_BEAN_LOAD);

            if (MSG_CHECK.contains(checkName)){
                String openFeignLoad = environment.getProperty(checkLoad);
                if (!StringUtils.equals(TAG,openFeignLoad)){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
}
