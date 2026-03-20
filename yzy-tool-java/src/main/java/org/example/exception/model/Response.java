package org.example.exception.model;

import org.example.exception.ExceptionEnum;

/**
 * Created by admin on 2018/3/5.
 */
public interface Response {
     //操作是否成功
     boolean SUCCESS = ExceptionEnum.SUCCESS.success();
     //操作代码
     String SUCCESS_CODE = ExceptionEnum.SUCCESS.code();

     ExceptionEnum SUCCESS_ENUM = ExceptionEnum.SUCCESS;

}
