package com.seuunng.todolist.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					loginRequest.getEmail(),
					loginRequest.getPassword());

			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return ResponseEntity.ok().body("로그인 성공");
		} catch (Exception e) {
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
}
