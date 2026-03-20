package org.example.exception;

import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangzhenyu
 * */
@ToString
public enum ExceptionEnum implements ResultCode {
    INVALID_PARAM(false,"10000","非法参数:{0}"),
    SUCCESS(true,"0","操作成功！"),
    FAIL(false,"10003","操作失败！"),
    ERROR_MSG(false,"10004","{0}"),

    NULL(false,"10010","参数为空"),
    INSUFFICIENT_PERMISSIONS(false,"88888","权限不足！"),
    TOKEN_ERROR(false,"500","token 错误"),
    TOKEN_TIMEOUT(false,"500","token 过期"),
    ERROR(false,"99999","抱歉，系统繁忙，请稍后重试！");


    /**
     * 操作是否成功
     */
    boolean success;

    /**
     * 枚举编码
     */
    private String code;
    /**
     * 枚举说明
     */
    private String desc;

    ExceptionEnum(boolean success,String code, String desc) {
        this.success = success;
        this.code = code;
        this.desc = desc;
    }

   /**
    * 根据code 查询 desc
    * */
    public static  String getDesc(String code){
        if(StringUtils.isEmpty(code)) {
            return null;
        }
        List<String> list  = new ArrayList<>();
        Arrays.stream(ExceptionEnum.values()).filter(t->t.code.equals(code)).forEach(v->{
            list.add(v.desc);
        });

        return list.size()>0?list.get(0):"";
    }

    /**
     * 根据code获取枚举
     */
    public static ExceptionEnum valueOfCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return ExceptionEnum.ERROR;
        }
        return Arrays.stream(ExceptionEnum.values()).filter(t -> t.code.equals(code)).findAny().orElse(ExceptionEnum.ERROR);
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
