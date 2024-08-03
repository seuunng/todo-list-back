package com.seuunng.todolist.login;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class SessionController {
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@GetMapping("/session")
	public ResponseEntity<?> checkSession(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			return ResponseEntity.status(401).body("사용자가 인증되지 않았습니다.");
		}
		String token = header.substring(7);
		if (jwtTokenProvider.validateToken(token)) {
			Claims claims = jwtTokenProvider.getClaimsFromToken(token);
			String email = claims.getSubject();

			// 이메일로 사용자 정보 조회
			Optional<UsersEntity> optionalUser = usersRepository.findByEmail(email);
			if (optionalUser.isPresent()) {
				UsersEntity user = optionalUser.get();
				System.out.println("3 User is authenticated: " + user);
				return ResponseEntity.ok(user);
			} else {
				return ResponseEntity.status(401).body("사용자가 인증되지 않았습니다.");
			}
		} else {
			return ResponseEntity.status(401).body("JWT 토큰이 유효하지 않습니다.");
		}
	}
}
