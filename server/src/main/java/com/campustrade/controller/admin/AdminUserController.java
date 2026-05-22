package com.campustrade.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.BusinessException;
import com.campustrade.common.Result;
import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;

    @GetMapping
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        var q = new LambdaQueryWrapper<User>()
            .ne(User::getRole, "admin");
        if (keyword != null && !keyword.isBlank()) {
            q.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        }
        q.orderByDesc(User::getCreatedAt);
        return Result.success(userMapper.selectPage(new Page<>(page, pageSize), q));
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        if ("admin".equals(user.getRole())) throw new BusinessException("不能禁用管理员");
        user.setStatus(body.get("status"));
        userMapper.updateById(user);
        return Result.success(null, "操作成功");
    }
}
