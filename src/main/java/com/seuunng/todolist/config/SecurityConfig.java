package com.seuunng.todolist.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(UserDetailsService userDetailsService, ObjectMapper objectMapper, AuthSuccessHandler authSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.authSuccessHandler = authSuccessHandler;
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화, csrf(Cross site Request forgery) : 테러자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 요구하지 않는 요청을 전달하는 것입니다. 즉, 사용자가 요구하지 않는 위조요청을 보내는 것을 의미
		.httpBasic(AbstractHttpConfigurer::disable) //HttpBasic() : Http basic Auth 기반으로 로그인 인증창이 뜬다.
		.formLogin(AbstractHttpConfigurer::disable)
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(authorize -> authorize
        		.requestMatchers( "/tasks/**", "/lists/**", "/auth/**","/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json").permitAll() // 인증 없이 접근 가능한 엔드포인트
        	
        		.requestMatchers("/admin/**").hasRole("ADMIN") 
        		.anyRequest().authenticated() // 나머지는 인증 필요
        )
            .formLogin(form -> form
                .loginPage("/login") //loginPage() : 로그인 페이지 URL
                .successHandler(authSuccessHandler)
                .defaultSuccessUrl("/monthlyBoard", true) // defaultSuccessURL() : 로그인 성공시 이동 URL
                .permitAll()//failureURL() : 로그인 실패시 URL
            )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/mainAccountInfo")
                    .defaultSuccessUrl("/monthlyBoard", true) // OAuth2 로그인 후 리디렉션
                )
			.logout(logout -> logout
					.logoutSuccessUrl("/mainAccountInfo")
					.invalidateHttpSession(true))//validateHttpSession() : 로그인아웃 이후 전체 세션 삭제 여부
            
            .sessionManagement(session -> session //sessionManagement() : 세션 생성 및 사용 여부에 대한 설정
            		 .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.builder()
//            .username("user")
//            .password(passwordEncoder.encode("password"))
//            .roles("USER")
//            .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
    	 return http.getSharedObject(AuthenticationManagerBuilder.class)
                 .userDetailsService(userDetailsService)
                 .passwordEncoder(passwordEncoder())
                 .and()
                 .build();
    }
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) {
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(true);
                sessionCookieConfig.setSecure(false); // HTTPS 환경에서는 true로 설정
            }
        };
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring(
        		).requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
