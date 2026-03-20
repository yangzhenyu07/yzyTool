package org.example.exception;


public interface ResultCode {
    //操作是否成功,true为成功，false操作失败
    boolean success();
    //操作代码
    String code();
    //提示信息
    String desc();

    void setDesc(String desc);



}
