package com.seuunng.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // CSRF 비활성화, csrf(Cross site Request forgery) : 테러자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 요구하지 않는 요청을 전달하는 것입니다. 즉, 사용자가 요구하지 않는 위조요청을 보내는 것을 의미
		.httpBasic(AbstractHttpConfigurer::disable) //HttpBasic() : Http basic Auth 기반으로 로그인 인증창이 뜬다.
		.formLogin(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
        		.requestMatchers( "/auth/**","/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json", "/logo192.png").permitAll() // 인증 없이 접근 가능한 엔드포인트
        		.requestMatchers("/admin/**").hasRole("ADMIN") 
        		.anyRequest().authenticated() // 나머지는 인증 필요
        )
            .formLogin(form -> form
                .loginPage("/login") //loginPage() : 로그인 페이지 URL
                .defaultSuccessUrl("/monthlyBoard", true) // defaultSuccessURL() : 로그인 성공시 이동 URL
                .permitAll()//failureURL() : 로그인 실패시 URL
            )
			.logout((logout) -> logout
					.logoutSuccessUrl("/logout")
					.invalidateHttpSession(true))//validateHttpSession() : 로그인아웃 이후 전체 세션 삭제 여부
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/mainAccountInfo")
                .defaultSuccessUrl("/monthlyBoard", true) // OAuth2 로그인 후 리디렉션
            )
            .sessionManagement(session -> session //sessionManagement() : 세션 생성 및 사용 여부에 대한 설정
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					//SessionCreationPolicy.Stateless : 4가지 중 하나로, 스프링 시큐리티가 생성되지 않고 존재하지 않습니다. (JWT와 같이 세션을 사용하지 않는 경우에 사용합니다)
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
}
