package com.seuunng.todolist.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuunng.todolist.login.CustomAuthenticationProvider;
import com.seuunng.todolist.login.CustomUserDetailsService;
import com.seuunng.todolist.login.JwtAuthenticationFilter;
//import com.seuunng.todolist.login.JwtRequestFilter;
import com.seuunng.todolist.login.JwtTokenProvider;
//import com.seuunng.todolist.login.OAuth2UserService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	@Autowired //JWT 토큰을 생성하고, 유효성을 검사하는 클래스 
	private JwtTokenProvider jwtTokenProvider;
    
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
	}

	public SecurityConfig(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean //CORS(Cross-Origin Resource Sharing) 설정을 정의
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000",
		        "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app"));
		config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
		config.setAllowedHeaders(Arrays.asList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean //애플리케이션의 보안 필터 체인을 정의
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(formLogin -> formLogin.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(authorize -> authorize
						// 인증 없이 접근 가능한 엔드포인트
						.requestMatchers("/", "/auth/**", "/login/**", "/api/session", "/oauth2/**", "/users/**").permitAll()
						.requestMatchers( "/index.html", "/static/**", "/favicon.ico","/manifest.json").permitAll()
						// 인증이 필요한 엔드포인트
						.requestMatchers("/tasks/**", "/lists/**","/monthlyBoard").hasRole("USER")
						.anyRequest().authenticated() // 나머지는 인증 필요
				).exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().write("{\"error\": \"Unauthorized\"}");
						}))
				.formLogin(form -> form.loginPage("/login")
						.failureHandler(authenticationFailureHandler()).defaultSuccessUrl("/monthlyBoard", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2.loginPage("/mainAccountInfo")
						.defaultSuccessUrl("/monthlyBoard", true)
			      )
				.logout(logout -> logout
						.logoutSuccessUrl("/mainAccountInfo")
						.invalidateHttpSession(true))
				.sessionManagement(session -> session // sessionManagement() : 세션 생성 및 사용 여부에 대한 설정
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS).maximumSessions(1) // 한 번에 하나의 세션만 허용
						.maxSessionsPreventsLogin(true).expiredUrl("/login?expired=true"))
				.securityContext(
						securityContext -> securityContext.securityContextRepository(securityContextRepository()));

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	
	@Bean //보안 컨텍스트 저장소
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean //비밀번호를 암호화
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean //인증 관리자를 설정
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.authenticationProvider(authenticationProvider())
				.build();
	}

	@Bean //세션 쿠키 설정을 초기화
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

	@Bean //정적 리소스에 대한 보안 설정을 무시하도록 설정
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}


	@Bean //커스텀 인증 제공자를 설정
	public AuthenticationProvider authenticationProvider() {
		return new CustomAuthenticationProvider();
	}

	@Bean //인증 실패 시 발생하는 예외를 처리
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, exception) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"message\": \"" + exception.getMessage() + "\"}");
		};
	}
}
