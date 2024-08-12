<<<<<<< HEAD
 package com.seuunng.todolist.config;
=======
package com.seuunng.todolist.config;
>>>>>>> origin/server

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
<<<<<<< HEAD
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
=======
>>>>>>> origin/server
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
	@Bean
	public WebMvcConfigurer corsConfig() {
		return new WebMvcConfigurer() {
<<<<<<< HEAD
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**")
						.addResourceLocations("classpath:/static/");
			}
=======
>>>>>>> origin/server

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
<<<<<<< HEAD
						.allowCredentials(true)
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
						.allowedHeaders("*"); // 쿠키를 허용하도록 설정
=======
						.allowedOrigins("http://localhost:3000")
						.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
						.allowedHeaders("*")
						.allowCredentials(true); // 쿠키를 허용하도록 설정
>>>>>>> origin/server
			}
		};
	}
}
