package com.gujjumarket.AgentManagmentSystem.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Configuration
public class SessionConfig {
    @Bean
    HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                HttpSession session = se.getSession();
                session.setMaxInactiveInterval(1800); // 30 minutes (adjust as needed)
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                // Optional: Handle session destruction if needed
            }
        };
    }
}
