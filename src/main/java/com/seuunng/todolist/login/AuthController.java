package com.seuunng.todolist.login;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


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
		ListsEntity defaultList = new ListsEntity();
        defaultList.setTitle("기본함");
        defaultList.setUser(user); // 리스트와 사용자 연결
        listsRepository.save(defaultList);
		return ResponseEntity.status(201).build();
	}
	
	 @PostMapping("/guest-login")
	    public ResponseEntity<?> guestLogin() {
	        UsernamePasswordAuthenticationToken token =
	            new UsernamePasswordAuthenticationToken("guest@gmail.com", "guest123");

	        Authentication authentication = authenticationManager.authenticate(token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        return ResponseEntity.ok().build();
	    }

	 @PostMapping("/logout")
	    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		 var auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth != null) {
	            new SecurityContextLogoutHandler().logout(request, response, auth);
	        }
	        return ResponseEntity.ok().build();
	    }
	 @GetMapping("/refresh-token")
	    public ResponseEntity<?> refreshToken(@AuthenticationPrincipal CustomUserDetails userDetails) {

	        UsersEntity user = userDetails.getUser();
	        String jwtToken = jwtTokenProvider.generateToken(userDetails.getUsername(), userDetails.getAuthorities().stream()
	            .map(auth -> auth.getAuthority())
	            .collect(Collectors.toList()));
	        return ResponseEntity.ok(new AuthResponse(user, jwtToken));
	    }
}
