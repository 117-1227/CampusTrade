package com.campustrade.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private String campus;
    private String role;
}
