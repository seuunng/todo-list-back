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
import org.springframework.security.core.userdetails.UserDetailsService;
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

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private final AuthSuccessHandler authSuccessHandler;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

//    @Autowired
//    private UserDetailsService userDetailsService;
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
	}
//    @Autowired
//    private DataSource dataSource;

	public SecurityConfig(CustomUserDetailsService userDetailsService, ObjectMapper objectMapper,
			AuthSuccessHandler authSuccessHandler) {

		this.userDetailsService = userDetailsService;
		this.objectMapper = objectMapper;
		this.authSuccessHandler = authSuccessHandler;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
		config.setAllowedHeaders(Arrays.asList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(formLogin -> formLogin.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(authorize -> authorize
						// 인증 없이 접근 가능한 엔드포인트
						.requestMatchers("/mainAccountInfo").permitAll()
						.requestMatchers("/auth/**", "/api/session", "/index.html", "/static/**", "/favicon.ico",
								"/manifest.json").permitAll()
						// 인증이 필요한 엔드포인트
						.requestMatchers("/tasks/**", "/lists/**").hasRole("USER")
						.anyRequest().authenticated() // 나머지는 인증 필요
				).exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json;charset=UTF-8");
							response.getWriter().write("{\"error\": \"Unauthorized\"}");
						}))
				.formLogin(form -> form.loginPage("/login").successHandler(authSuccessHandler)
						.failureHandler(authenticationFailureHandler()).defaultSuccessUrl("/monthlyBoard", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2.loginPage("/mainAccountInfo").defaultSuccessUrl("/monthlyBoard", true) // OAuth2
																														// 로그인
																														// 후
																														// 리디렉션
				).logout(logout -> logout.logoutSuccessUrl("/mainAccountInfo").invalidateHttpSession(true))// validateHttpSession()
																											// : 로그인아웃
																											// 이후 전체 세션
																											// 삭제 여부
				.sessionManagement(session -> session // sessionManagement() : 세션 생성 및 사용 여부에 대한 설정
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS).maximumSessions(1) // 한 번에 하나의 세션만 허용
						.maxSessionsPreventsLogin(true).expiredUrl("/login?expired=true"))
				.securityContext(
						securityContext -> securityContext.securityContextRepository(securityContextRepository()));

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

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
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//            .jdbcAuthentication()
//            .dataSource(dataSource)
//            .usersByUsernameQuery("select email, password, enabled from users where email=?")
//            .authoritiesByUsernameQuery("select email, authority from authorities where email=?")
//            .passwordEncoder(passwordEncoder());
//    }
	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).authenticationProvider(authenticationProvider())
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
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//			.authorizeRequests()
//			.antMatchers("/", "/users").permitAll()
//			.antMatchers("/mypage").hasRole("USER")
//			.antMatchers("/messages").hasRole("MANAGER")
//			.antMatchers("/config").hasRole("ADMIN")
//			.anyRequest().authenticated()
//			.and()
//			.formLogin()
//			.loginPage("/login")	// 커스텀 로그인 페이지 설정!
//			.loginProcessingUrl("/login_proc")	// 로그인 post url 지정
//			.defaultSuccessUrl("/")		// 로그인 성공 티폴트 redirect 경로
//			.permitAll(); // 커스텀 로그인 페이지를 설정했으니 permitAll 해준다.
//	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new CustomAuthenticationProvider();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, exception) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"message\": \"" + exception.getMessage() + "\"}");
		};
	}
}
