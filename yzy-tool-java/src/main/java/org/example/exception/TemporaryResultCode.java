package org.example.exception;
/**
* @description: TODO
* @author 杨镇宇
* @date 2024/12/11 15:32
* @version 1.0
*/

public class TemporaryResultCode implements ResultCode {

    private final ResultCode originalResultCode;
    private final String customDesc;

    public TemporaryResultCode(ResultCode originalResultCode, String customDesc) {
        this.originalResultCode = originalResultCode;
        this.customDesc = customDesc;
    }

    @Override
    public boolean success() {
        return originalResultCode.success();
    }

    @Override
    public String code() {
        return originalResultCode.code();
    }

    @Override
    public String desc() {
        return customDesc;
    }

    @Override
    public void setDesc(String desc) {
        throw new UnsupportedOperationException("TemporaryResultCode does not support setDesc");
    }
}
