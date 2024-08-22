package com.seuunng.todolist.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.ListsRepository;
import com.seuunng.todolist.lists.SmartListsEntity;
import com.seuunng.todolist.lists.SmartListsRepository;
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;
import com.seuunng.todolist.users.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:3000", "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app" })
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private ListsRepository listsRepository;
	@Autowired
	private SmartListsRepository smartListsRepository;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private UsersService userService;
	@Autowired
	private final JavaMailSender mailSender;

	public AuthController(JavaMailSender mailSender, UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
		this.mailSender = mailSender;
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("로그인 실행");
		try {
			if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
				throw new IllegalArgumentException("Email and password must not be null");
			}
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
					loginRequest.getPassword());

			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersEntity user = userDetails.getUser();
			  
			String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities()
					.stream().map(auth -> auth.getAuthority()).collect(Collectors.toList()));

			return ResponseEntity.ok(new AuthResponse(user, jwtToken)); //

		} catch (Exception e) {
			System.err.println("Login failed for email: " + loginRequest.getEmail());
			e.printStackTrace();
			return ResponseEntity.status(401).body("로그인 실패: " + e.getMessage());
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signupRequest) {
		if (usersRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
		}

		UsersEntity user = new UsersEntity();
		user.setNickname(signupRequest.getNickname());
		user.setEmail(signupRequest.getEmail());
		user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		usersRepository.save(user);

		ListsEntity defaultList = new ListsEntity();
		defaultList.setTitle("기본함");
		defaultList.setUser(user); // 리스트와 사용자 연결
		listsRepository.save(defaultList);
		
		
		if (user.getSmartList() == null) {

			SmartListsEntity defaultList_basic = new SmartListsEntity();
			defaultList_basic.setTitle("모든 할 일");
			defaultList_basic.setUser(user);
			defaultList_basic.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_basic.setIsDeleted(false);
			smartListsRepository.save(defaultList_basic);

			SmartListsEntity defaultList_today = new SmartListsEntity();
			defaultList_today.setTitle("오늘 할 일");
			defaultList_today.setUser(user);
			defaultList_today.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_today.setIsDeleted(false);
			smartListsRepository.save(defaultList_today);

			SmartListsEntity defaultList_tommorow = new SmartListsEntity();
			defaultList_tommorow.setTitle("내일 할 일");
			defaultList_tommorow.setUser(user);
			defaultList_tommorow.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_tommorow.setIsDeleted(false);
			smartListsRepository.save(defaultList_tommorow);

			SmartListsEntity defaultList_nextWeek = new SmartListsEntity();
			defaultList_nextWeek.setTitle("다음주 할 일");
			defaultList_nextWeek.setUser(user);
			defaultList_nextWeek.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_nextWeek.setIsDeleted(false);
			smartListsRepository.save(defaultList_nextWeek);

			SmartListsEntity defaultList_completed = new SmartListsEntity();
			defaultList_completed.setTitle("완료한 할 일");
			defaultList_completed.setUser(user);
			defaultList_completed.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_completed.setIsDeleted(false);
			smartListsRepository.save(defaultList_completed);

			SmartListsEntity defaultList_deleted = new SmartListsEntity();
			defaultList_deleted.setTitle("취소한 할 일");
			defaultList_deleted.setUser(user);
			defaultList_deleted.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
			defaultList_deleted.setIsDeleted(false);
			smartListsRepository.save(defaultList_deleted);
		}
		return ResponseEntity.status(201).build();
	}

	@PostMapping("/updatePW")
	public ResponseEntity<?> updatePW(@RequestBody UpdatePWRequest updatePWRequest) {

		Optional<UsersEntity> userOptional = usersRepository.findByEmail(updatePWRequest.getEmail());

		if (userOptional.isEmpty()) {
			return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
		}

		UsersEntity user = userOptional.get();

		// 현재 비밀번호와 새 비밀번호가 같은지 확인
		if (passwordEncoder.matches(updatePWRequest.getNewPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body("새 비밀번호가 기존 비밀번호와 같습니다. 다른 비밀번호를 입력하세요.");
		}

		// 새 비밀번호로 업데이트
		user.setPassword(passwordEncoder.encode(updatePWRequest.getNewPassword()));
		usersRepository.save(user);

		return ResponseEntity.status(201).build();
	}

	@PostMapping("/findPW")
	public ResponseEntity<?> findPassword(@RequestBody EmailRequest emailRequest) {
		Optional<UsersEntity> userOptional = usersRepository.findByEmail(emailRequest.getEmail());

		if (userOptional.isEmpty()) {
			return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
		}

		UsersEntity user = userOptional.get();

		// 랜덤 비밀번호 생성
		String newPassword = generateRandomPassword();
		user.setPassword(passwordEncoder.encode(newPassword));
		usersRepository.save(user);

		// 이메일 발송
		sendEmail(user.getEmail(), newPassword);

		return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
	}

	private String generateRandomPassword() {
		int length = 10;
		String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder password = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(charSet.length());
			password.append(charSet.charAt(index));
		}

		return password.toString();
	}

	private void sendEmail(String to, String newPassword) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("임시 비밀번호 안내");
		message.setText("임시 비밀번호는 " + newPassword + " 입니다. 로그인 후 비밀번호를 변경해주세요.");
		// 연결 도메인 추가@!
		mailSender.send(message);
	}

	@PostMapping("/guest-login")
	public ResponseEntity<?> guestLogin() {

		String email = "guest@gmail.com";
		String password = "guest123";

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 사용자 정보를 바탕으로 JWT 토큰 생성
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(),
				userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		Map<String, Object> response = new HashMap<>();
		response.put("token", jwtToken);
		response.put("user", userDetails.getUser());
		// 클라이언트로 토큰 반환
		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/google")
	public ResponseEntity<?> googleLogin(@RequestBody TokenDto tokenDto) throws GeneralSecurityException, IOException {

		String accessToken = tokenDto.getAccessToken();
		String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

		URL url = new URL(userInfoEndpoint);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

		int responseCode = conn.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject jsonObject = new JSONObject(response.toString());

			String email = jsonObject.getString("email");
			String name = jsonObject.getString("given_name");
			// 필요한 추가 사용자 정보 가져오기
			UsersEntity user = usersRepository.findByEmail(email).orElseGet(() -> {
				UsersEntity newUser = new UsersEntity();
				newUser.setEmail(email);
				newUser.setNickname(name);
				newUser.setPassword(""); // 임시 비밀번호 설정
				usersRepository.save(newUser);
				return newUser;
			});

			if (user.getLists() == null) {
				ListsEntity defaultList = new ListsEntity();
				defaultList.setTitle("기본함");
				defaultList.setUser(user);
				listsRepository.save(defaultList);
			}
			if (user.getSmartList() == null) {

				SmartListsEntity defaultList_basic = new SmartListsEntity();
				defaultList_basic.setTitle("모든 할 일");
				defaultList_basic.setUser(user);
				defaultList_basic.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_basic.setIsDeleted(false);
				smartListsRepository.save(defaultList_basic);

				SmartListsEntity defaultList_today = new SmartListsEntity();
				defaultList_today.setTitle("오늘 할 일");
				defaultList_today.setUser(user);
				defaultList_today.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_today.setIsDeleted(false);
				smartListsRepository.save(defaultList_today);

				SmartListsEntity defaultList_tommorow = new SmartListsEntity();
				defaultList_tommorow.setTitle("내일 할 일");
				defaultList_tommorow.setUser(user);
				defaultList_tommorow.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_tommorow.setIsDeleted(false);
				smartListsRepository.save(defaultList_tommorow);

				SmartListsEntity defaultList_nextWeek = new SmartListsEntity();
				defaultList_nextWeek.setTitle("다음주 할 일");
				defaultList_nextWeek.setUser(user);
				defaultList_nextWeek.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_nextWeek.setIsDeleted(false);
				smartListsRepository.save(defaultList_nextWeek);

				SmartListsEntity defaultList_completed = new SmartListsEntity();
				defaultList_completed.setTitle("완료한 할 일");
				defaultList_completed.setUser(user);
				defaultList_completed.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_completed.setIsDeleted(false);
				smartListsRepository.save(defaultList_completed);

				SmartListsEntity defaultList_deleted = new SmartListsEntity();
				defaultList_deleted.setTitle("취소한 할 일");
				defaultList_deleted.setUser(user);
				defaultList_deleted.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
				defaultList_deleted.setIsDeleted(false);
				smartListsRepository.save(defaultList_deleted);
			}
			
			user = usersRepository.findByEmail(email).orElseGet(() -> {
				UsersEntity newUser = new UsersEntity();
				newUser.setEmail(email);
				newUser.setNickname(name);
				newUser.setPassword(""); // 임시 비밀번호 설정
				usersRepository.save(newUser);
				return newUser;
			});
			
			CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
			String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities()
					.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("token", jwtToken);
			responseMap.put("user", user);
			return ResponseEntity.ok(responseMap);
		} else {
			return ResponseEntity.status(401).body("Invalid access token.");
		}

	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		System.out.println("Authorization header: " + authHeader); // 로그 추가

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(401).body("Invalid Token");
		}

		String refreshToken = authHeader.substring(7);

		System.out.println("Refresh Token: " + refreshToken); // 로그 추가

		if (jwtTokenProvider.validateToken(refreshToken)) {
			String email = jwtTokenProvider.getEmailFromToken(refreshToken);
			// 새 액세스 토큰 및 리프레시 토큰 생성
			String newAccessToken = jwtTokenProvider.generateToken(email, List.of("ROLE_USER")); // 역할을 적절히 설정
			String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

			return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
		} else {
			return ResponseEntity.status(303).header("Location", "/mainAccountInfo").build();
		}
	}

	@GetMapping("/session")
	public ResponseEntity<?> getSession() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UserDTO user = userService.getUserWithLists(userDetails.getUsername());
			return ResponseEntity.ok(userDetails);
		} else {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Unauthorized");
		}
	}
}
