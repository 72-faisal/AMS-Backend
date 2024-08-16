package com.gujjumarket.AgentManagmentSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gujjumarket.AgentManagmentSystem.utils.JWT;

@Configuration
public class JWTConfig {

	@Value("${jwtsecret}")
	private String jwtsecret;

	@Value("${jwttimeout}")
	private Long jwttimeout;

    @Bean
    JWT jwt() {
		return new JWT(jwtsecret, jwttimeout);
	}
    
}
