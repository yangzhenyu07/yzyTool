package org.example.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
/**
* @author 杨镇宇
* @date 2025/2/14 14:11
* @version 1.0
*/
@Data
public class User {

    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50之间")
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String password;



    // 构造函数、Getter和Setter
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


}
