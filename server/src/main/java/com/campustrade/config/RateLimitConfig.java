package com.campustrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/** 简易登录限流：同一 IP 60 秒内最多 5 次失败尝试 */
@Configuration
public class RateLimitConfig {

    @Bean
    public ConcurrentHashMap<String, LoginAttempt> loginAttempts() {
        return new ConcurrentHashMap<>();
    }

    public static class LoginAttempt {
        private int count;
        private long resetTime;

        public LoginAttempt() { this.resetTime = System.currentTimeMillis() + 60_000; }

        public boolean isBlocked() {
            if (System.currentTimeMillis() > resetTime) { count = 0; resetTime = System.currentTimeMillis() + 60_000; return false; }
            return count >= 5;
        }

        public void increment() { count++; }
    }
}
