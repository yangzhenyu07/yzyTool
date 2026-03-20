package org.example.util;

import org.example.vo.User;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 校验工具类
* @author 杨镇宇
* @date 2025/2/14 13:55
* @version 1.0
*/

public class ValidatorUtils {
    private static final Validator FIRST_VALIDATOR;

    static {
        // failFast(true) 表示一旦发现校验失败就停止，不继续校验其他约束条件。
        FIRST_VALIDATOR = Validation.byProvider(HibernateValidator.class)
                .configure().failFast(true).buildValidatorFactory().getValidator();
    }

    /**
     * 是否有效
     * @param obj
     * @return
     */
    public static boolean isValid(Object obj){
        Set<ConstraintViolation<Object>> errors = FIRST_VALIDATOR.validate(obj);
        return 0 == errors.size() ;
    }

    /**
     * 验证
     * @param obj
     * @param groups
     * @return
     */
    public static String validate(Object obj,Class<?> ... groups){
        Set<ConstraintViolation<Object>> errors = FIRST_VALIDATOR.validate(obj, groups);
        if (0 == errors.size()){
            return null;
        }
        ConstraintViolation<Object> next = errors.iterator().next();
        return next.getMessage();

    }

    public static void main(String[] args) {

        // 创建一个无效的 User 对象（用户名为空，邮箱格式错误）
        User user = new User("", "12345");

        // 使用 isValid 方法检查对象是否有效
        System.out.println("isValid: " + isValid(user)); // false

        // 使用 validate 方法检查错误并返回第一个错误消息
        System.out.println("Validation error: " + validate(user)); // "用户名不能为空"

        // 创建一个有效的 User 对象
        User validUser = new User("JohnDoe", "securePassword123");

        // 校验有效对象
        System.out.println("isValid: " + isValid(validUser)); // true
        System.out.println("Validation error: " + validate(validUser)); // null

    }
}
