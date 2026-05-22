package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.AuthService;
import com.campustrade.vo.LoginVO;
import com.campustrade.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserVO register(String username, String password, String nickname, String phone) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException("密码长度至少6位");
        }

        long count = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : "");
        user.setPhone(phone != null ? phone : "");
        user.setRole("user");
        user.setStatus("active");
        userMapper.insert(user);

        return toVO(user);
    }

    @Override
    public LoginVO login(String username, String password) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(
            user.getId(), user.getUsername(), user.getRole());

        LoginVO result = new LoginVO();
        result.setToken(token);
        result.setUser(toVO(user));
        return result;
    }

    @Override
    public UserVO getMe(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toVO(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setCampus(user.getCampus());
        vo.setRole(user.getRole());
        return vo;
    }
}
