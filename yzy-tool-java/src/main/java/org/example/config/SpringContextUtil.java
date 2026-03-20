package org.example.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * spring 上下文工具类
 * @author yangzhenyu
 **/
@Component("SpringContextUtil")
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过名称和Class获取对象实例
     *
     * @param beanName bean的名称
     * @param <T>      泛型
     * @return 对象实例
     */
    public static <T> T getBean(String beanName) {
        try {
            if (beanName == null) {
                return null;
            }
            return (T) applicationContext.getBean(beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过名称和Class获取对象实例
     *
     * @param beanName bean的名称
     * @param <T>      泛型
     * @return 对象实例
     */
    public static <T> T getBeanOrNull(String beanName) {
        try {
            if (beanName == null) {
                return null;
            }
            return (T) applicationContext.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过名称和Class获取对象实例
     *
     * @param requiredType bean的Class
     * @param <T>          泛型
     * @return 对象实例
     */
    public static <T> T getBean(Class<T> requiredType) {
        try {
            if (requiredType == null) {
                return null;
            }
            return (T) applicationContext.getBean(requiredType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 通过名称和Class获取对象实例
     *
     * @param requiredType bean的Class
     * @param <T>          泛型
     * @return 对象实例
     */
    public static <T> T getBeanOrNull(Class<T> requiredType) {
        try {
            if (requiredType == null) {
                return null;
            }
            return (T) applicationContext.getBean(requiredType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过名称和Class获取对象实例
     *
     * @param beanName     bean的名称
     * @param requiredType bean的Class
     * @param <T>          泛型
     * @return 对象实例
     */
    public static <T> T getBean(String beanName, Class<T> requiredType) {
        try {
            if (requiredType == null) {
                return null;
            }
            return (T) applicationContext.getBean(beanName, requiredType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 通过名称和Class获取对象实例
     *
     * @param beanName     bean的名称
     * @param requiredType bean的Class
     * @param <T>          泛型
     * @return 对象实例
     */
    public static <T> T getBeanOrNull(String beanName, Class<T> requiredType) {
        try {
            if (requiredType == null) {
                return null;
            }
            return (T) applicationContext.getBean(beanName, requiredType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过固定的class 获取一组实现类
     * @param requiredType 基类Class
     * @param <T> 泛型
     * @return map
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
        try {
            if (requiredType == null) {
                return null;
            }
            return (Map<String, T>) applicationContext.getBeansOfType(requiredType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取请求对象
     *
     * @return 请求对象
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    /**
     * 获取请求对象
     *
     * @return 请求对象
     */
    public static HttpServletResponse getHttpServletResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return  ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        return null;
    }

    /**
     * 获取注解下的所有对象实例
     *
     * @param annotationType 注解的Class
     * @return 对象实例
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return null;
        }
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    /**
     * 通过请求获取Web上下文对象
     *
     * @param request 请求对象
     * @return web上下文对象
     */
    public static WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
    }
}
