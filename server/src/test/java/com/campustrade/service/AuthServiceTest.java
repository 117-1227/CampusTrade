package com.campustrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campustrade.common.BusinessException;
import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import com.campustrade.security.JwtTokenProvider;
import com.campustrade.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
            "test-secret-key-that-is-long-enough-for-hs256", 3600000L);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(userMapper, passwordEncoder, jwtTokenProvider);
    }

    // ====== 注册 ======

    @Test
    void shouldRegisterNewUserSuccessfully() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        var result = authService.register("newuser", "123456", "新用户", "13800138000");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertTrue(passwordEncoder.matches("123456", saved.getPassword()));
        assertEquals("user", saved.getRole());
        assertEquals("active", saved.getStatus());
    }

    @Test
    void shouldRejectDuplicateUsername() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(BusinessException.class, () ->
            authService.register("existing", "123456", "用户", "13800138000"));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void shouldRejectEmptyUsername() {
        assertThrows(BusinessException.class, () ->
            authService.register("", "123456", "用户", "13800138000"));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void shouldRejectShortPassword() {
        assertThrows(BusinessException.class, () ->
            authService.register("user", "12345", "用户", "13800138000"));
        verify(userMapper, never()).insert(any());
    }

    // ====== 登录 ======

    @Test
    void shouldLoginSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("correct"));
        user.setNickname("测试");
        user.setRole("user");
        user.setStatus("active");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        var result = authService.login("testuser", "correct");

        assertNotNull(result.getToken());
        assertEquals("testuser", result.getUser().getUsername());
        assertTrue(jwtTokenProvider.validateToken(result.getToken()));
    }

    @Test
    void shouldRejectWrongPassword() {
        User user = new User();
        user.setPassword(passwordEncoder.encode("correct"));
        user.setStatus("active");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        assertThrows(BusinessException.class, () ->
            authService.login("testuser", "wrong"));
    }

    @Test
    void shouldRejectDisabledUserLogin() {
        User user = new User();
        user.setPassword(passwordEncoder.encode("correct"));
        user.setStatus("disabled");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        assertThrows(BusinessException.class, () ->
            authService.login("testuser", "correct"));
    }

    @Test
    void shouldRejectNonExistentUser() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(BusinessException.class, () ->
            authService.login("nobody", "any"));
    }

    // ====== getMe ======

    @Test
    void shouldReturnCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("测试");
        user.setRole("user");
        user.setStatus("active");

        when(userMapper.selectById(1L)).thenReturn(user);

        var result = authService.getMe(1L);

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () ->
            authService.getMe(999L));
    }
}
