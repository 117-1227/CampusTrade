package com.campustrade.service;

import com.campustrade.vo.LoginVO;
import com.campustrade.vo.UserVO;

public interface AuthService {

    UserVO register(String username, String password, String nickname, String phone);

    LoginVO login(String username, String password);

    UserVO getMe(Long userId);
}
