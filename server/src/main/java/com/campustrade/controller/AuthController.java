package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.dto.LoginDTO;
import com.campustrade.dto.RegisterDTO;
import com.campustrade.service.AuthService;
import com.campustrade.vo.LoginVO;
import com.campustrade.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        UserVO user = authService.register(
            dto.getUsername(), dto.getPassword(), dto.getNickname(), dto.getPhone());
        return Result.success(user, "注册成功");
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO result = authService.login(dto.getUsername(), dto.getPassword());
        return Result.success(result, "登录成功");
    }

    @GetMapping("/me")
    public Result<UserVO> me(@RequestAttribute("userId") Long userId) {
        UserVO user = authService.getMe(userId);
        return Result.success(user);
    }
}
