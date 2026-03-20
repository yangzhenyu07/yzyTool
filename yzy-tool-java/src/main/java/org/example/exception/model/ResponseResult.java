package org.example.exception.model;

import lombok.Data;
import lombok.ToString;
import org.example.exception.ResultCode;
import org.example.vo.LogTracerVo;

import java.io.Serializable;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 18:33.
 * @Modified By:
 */
@Data
@ToString
public class ResponseResult implements Response, Serializable {

    //操作是否成功
    boolean success = SUCCESS;

    //操作代码
    String code = SUCCESS_CODE;

    //traceId
    String traceId;

    //ip
    String ip;

    //提示信息
    String message;

    //返回的数据
    Object data;




    public static ResponseResult ok(Object _datas){
        return new ResponseResult(SUCCESS_ENUM,_datas);
    }
    public ResponseResult(ResultCode resultCode){
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.message = resultCode.desc();
    }



    public ResponseResult(ResultCode resultCode, Object _datas){
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.message = resultCode.desc();
        this.data = _datas;
    }



}
