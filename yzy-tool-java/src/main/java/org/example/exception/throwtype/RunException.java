package org.example.exception.throwtype;



import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ResultCode;
import org.example.exception.TemporaryResultCode;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 自定义异常类型
 * @author yangzhenyu
 * @version 1.0
 * @create 2018-09-14 17:28
 **/
public class RunException extends RuntimeException implements Serializable {

    //错误代码
    ResultCode resultCode;

    public RunException(ResultCode resultCode){
        this.resultCode = resultCode;
    }

    public RunException(ResultCode resultCode,String desc){
        if (StringUtils.isNotEmpty(desc)){
            String oldDesc = resultCode.desc();
            if (oldDesc.contains("{0}")){
                // 格式化描述字符串，不修改枚举的原始值
                String formattedDesc = MessageFormat.format(oldDesc, desc);
                this.resultCode = new TemporaryResultCode(resultCode, formattedDesc);
                return;
            }
        }
        this.resultCode = resultCode;
    }


    public RunException(ResultCode resultCode,String ... desc){
        if ( ObjectUtil.isNotNull(desc) && desc.length>0){
            String oldDesc = resultCode.desc();
            if (oldDesc.contains("{0}")){
                // 格式化描述字符串，不修改枚举的原始值
                String formattedDesc = MessageFormat.format(oldDesc, desc);
                this.resultCode = new TemporaryResultCode(resultCode, formattedDesc);
                return;
            }
        }
        this.resultCode = resultCode;
    }


    public ResultCode getResultCode(){
        return resultCode;
    }


}
