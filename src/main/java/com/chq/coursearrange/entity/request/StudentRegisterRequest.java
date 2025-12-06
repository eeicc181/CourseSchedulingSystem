package com.chq.coursearrange.entity.request;

import com.chq.coursearrange.validation.StudentNo;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 学生注册请求
 * 
 * @author CHQ
 * @version 2.0.0
 * @since 2024-12-06
 */
@Data
public class StudentRegisterRequest {

    @NotBlank(message = "学号不能为空")
    @StudentNo(message = "学号格式不正确，应为10位数字")
    private String studentNo;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", 
             message = "密码必须包含字母和数字，长度6-20位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度必须在2-20之间")
    private String realname;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @Size(max = 100, message = "地址长度不能超过100个字符")
    private String address;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String telephone;

    @Email(message = "邮箱格式不正确")
    private String email;

}
