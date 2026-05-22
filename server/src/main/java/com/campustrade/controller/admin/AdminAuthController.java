package com.campustrade.controller.admin;

import com.campustrade.common.Result;
import com.campustrade.dto.LoginDTO;
import com.campustrade.service.AuthService;
import com.campustrade.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO result = authService.login(dto.getUsername(), dto.getPassword());
        if (!"admin".equals(result.getUser().getRole())) {
            return Result.error("无管理员权限");
        }
        return Result.success(result, "登录成功");
    }
}
