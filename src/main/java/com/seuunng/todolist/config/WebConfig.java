
 package com.seuunng.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
	@Bean
	public WebMvcConfigurer corsConfig() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**")
						.addResourceLocations("classpath:/static/");
			}

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowCredentials(true)
						.allowedOrigins(
								"http://localhost:3000",
	                            "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app"  // 클라우드타입 URL 추가
		                        )
						.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
						.allowedHeaders("*"); // 쿠키를 허용하도록 설정
			}
		};
	}
}

