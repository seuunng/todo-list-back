package com.seuunng.todolist.login;

import javax.crypto.SecretKey;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
    
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,  HttpServletRequest request, HttpServletResponse response) {
		try {
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
	                loginRequest.getEmail(),
	                loginRequest.getPassword()
	        );

	        Authentication authentication = authenticationManager.authenticate(token);
	        
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
            Authentication authCheck = SecurityContextHolder.getContext().getAuthentication();

	        AccountContext accountContext = (AccountContext) authentication.getPrincipal();

            System.out.println("1 Authentication Principal: " + accountContext);
            
	        UsersEntity user = accountContext.getAccount();

            System.out.println("2 Authentication Principal: " + user);
            
//	        request.getSession().setAttribute("user", user);
	        
	        return ResponseEntity.ok(new AuthResponse(user)); //
	        
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
}
