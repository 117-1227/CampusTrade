package com.campustrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为3-50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度至少6位")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不超过50个字符")
    private String nickname;

    @Size(max = 20, message = "手机号不超过20个字符")
    private String phone;
}
