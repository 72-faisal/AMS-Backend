	package com.gujjumarket.AgentManagmentSystem.config;
	
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
	import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
	
	import com.gujjumarket.AgentManagmentSystem.interceptor.MyInterceptor;
	
	@Configuration
	public class RestApiConfig implements WebMvcConfigurer{
			    @Bean
			    MyInterceptor myInterceptor() {
					return new MyInterceptor();
				}
			
				@Override
				public void addInterceptors(InterceptorRegistry registry) {
					registry.addInterceptor(myInterceptor()).addPathPatterns("/**"); 
				}
	}
