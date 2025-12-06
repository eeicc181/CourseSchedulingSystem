package com.chq.coursearrange.entity.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求
 * 
 * @author CHQ
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
public class UserLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名长度必须在5-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    /**
     * 1管理员，2讲师
     */
    private Integer type;

}
