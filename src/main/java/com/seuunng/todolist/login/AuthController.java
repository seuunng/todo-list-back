package com.seuunng.todolist.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;
import com.seuunng.todolist.users.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
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
    private UserDetailsService userDetailsService;
    @Autowired
    private UsersService userService;
    
    private static final String CLIENT_ID = "834919745048-tiu8j0gnrtsl3f72m5cdkbsk05basoqo.apps.googleusercontent.com";
	
    @PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,  HttpServletRequest request, HttpServletResponse response) {
		try {
			if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
	            throw new IllegalArgumentException("Email and password must not be null");
	        }
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
	                loginRequest.getEmail(),
	                loginRequest.getPassword()
	        );

	        Authentication authentication = authenticationManager.authenticate(token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        UsersEntity user = userDetails.getUser();
	        String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
	                .map(auth -> auth.getAuthority())
	                .collect(Collectors.toList()));
            System.out.println("1 Authentication Principal: " + userDetails);
	        
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
		
		if (user.getLists() == null ) {
		ListsEntity defaultList = new ListsEntity();
        defaultList.setTitle("기본함");
        defaultList.setUser(user); // 리스트와 사용자 연결
        listsRepository.save(defaultList);
		};

		return ResponseEntity.status(201).build();
	}
	
	 @PostMapping("/guest-login")
	    public ResponseEntity<?> guestLogin() {

		    String email = "mnb2856@gmail.com";
		    String password = "guest123";
		    
	        UsernamePasswordAuthenticationToken token =
	            new UsernamePasswordAuthenticationToken(email, password);
	        
	        Authentication authentication = authenticationManager.authenticate(token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // 사용자 정보를 바탕으로 JWT 토큰 생성
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
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
		        
		        ListsEntity defaultList = new ListsEntity();
		        defaultList.setTitle("기본함");
		        defaultList.setUser(user); // 리스트와 사용자 연결
		        listsRepository.save(defaultList);
		        
		        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
		        String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
		                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

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
	            
	            return ResponseEntity.ok(Map.of(
	                    "accessToken", newAccessToken,
	                    "refreshToken", newRefreshToken
	            ));
	        } else {
	            return ResponseEntity.status(401).body("Invalid Refresh Token");
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
