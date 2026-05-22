package com.campustrade.config;

import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import com.campustrade.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("h2")
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Admin 由 DataInitializer 创建
        User admin = userMapper.selectById(1L);
        adminToken = jwtTokenProvider.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        // 创建或获取普通用户
        userToken = getOrCreateNormalUser();
    }

    private String getOrCreateNormalUser() {
        var q = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
            .eq(User::getUsername, "normaltest");
        User u = userMapper.selectOne(q);
        if (u == null) {
            u = new User();
            u.setUsername("normaltest");
            u.setPassword(passwordEncoder.encode("pass123"));
            u.setNickname("Normal");
            u.setRole("user");
            u.setStatus("active");
            userMapper.insert(u);
        }
        return jwtTokenProvider.generateToken(u.getId(), u.getUsername(), u.getRole());
    }

    @Test
    void shouldAllowAnonymousToBrowseProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk());  // 不是 401/403，说明端点公开
    }

    @Test
    void shouldAllowAnonymousToGetCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldBlockUnauthenticatedFromMe() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAuthenticatedToAccessMe() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldBlockNormalUserFromAdminProductAudit() throws Exception {
        mockMvc.perform(get("/api/admin/products/pending")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldBlockNormalUserFromAdminUserList() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/products/pending")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAnonymousToAccessSellerPage() throws Exception {
        mockMvc.perform(get("/api/users/1/profile"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldBlockAnonymousFromMe() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotExposeSensitiveFieldsInSellerResponse() throws Exception {
        String body = mockMvc.perform(get("/api/users/1/profile"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assert !body.contains("\"password\"") : "seller response MUST NOT contain password";
        assert !body.contains("\"phone\"") : "seller response MUST NOT contain phone";
        assert !body.contains("\"role\"") : "seller response MUST NOT contain role";
        assert !body.contains("\"status\"") : "seller response MUST NOT contain status";
    }

    @Test
    void shouldBlockAnonymousFromOrders() throws Exception {
        mockMvc.perform(get("/api/orders/buy"))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/orders"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAuthenticatedToAccessOwnOrders() throws Exception {
        mockMvc.perform(get("/api/orders/buy")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
            .andExpect(status().isOk());
    }
}
