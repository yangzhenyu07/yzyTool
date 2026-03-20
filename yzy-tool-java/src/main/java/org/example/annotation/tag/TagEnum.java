package org.example.annotation.tag;

import org.apache.commons.lang3.StringUtils;
import org.example.annotation.constant.SchedulerTask;

import java.util.Arrays;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2024/12/19 15:55
 */
public enum TagEnum {
    SUCCESS_TAG(SchedulerTask.SUCCESS, "成功" ),
    ERROR_TAG(SchedulerTask.ERROR, "失败");

    private final String code;
    private final String name;


    TagEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TagEnum valueOfCode(String code) {
        if(StringUtils.isEmpty(code)) return TagEnum.SUCCESS_TAG;
        return Arrays.stream(TagEnum.values()).filter(t->t.code.equals(code)).findAny().orElse(TagEnum.SUCCESS_TAG);
    }

    public static TagEnum valueOfName(String name) {
        if(StringUtils.isEmpty(name)) return TagEnum.SUCCESS_TAG;
        return Arrays.stream(TagEnum.values()).filter(t->t.name.equals(name)).findAny().orElse(TagEnum.SUCCESS_TAG);
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
