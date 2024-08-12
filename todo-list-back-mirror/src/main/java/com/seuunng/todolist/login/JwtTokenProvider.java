package com.seuunng.todolist.login;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKeyString;

	@Value("${jwt.expiration}")
    private long validityInMilliseconds; // 1h

    @Value("${jwt.refreshExpiration}")
    private long refreshValidityInMilliseconds;
    
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
	}

	public String generateToken(String email, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("role", roles);
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
	    
		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();

		return token;
	}

	public Claims getClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

	public String getEmailFromToken(String token) {
		 return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }
	public String createRefreshToken(String email) {
	    Claims claims = Jwts.claims().setSubject(email);
	    Date now = new Date();
	    Date validity = new Date(now.getTime() + refreshValidityInMilliseconds);

	    String refreshToken = Jwts.builder()
	            .setClaims(claims)
	            .setIssuedAt(now)
	            .setExpiration(validity)
	            .signWith(SignatureAlgorithm.HS256, secretKey)
	            .compact();

	    return refreshToken;
	}
	public boolean validateToken(String token) {
		try {
			if (token == null || token.trim().isEmpty()) {
				System.out.println("token"+token);
				throw new MalformedJwtException("Empty JWT token");
			}
			long dotCount = token.chars().filter(ch -> ch == '.').count();
			if (dotCount != 2) {
				throw new MalformedJwtException(
						"JWT strings must contain exactly 2 period characters. Found: " + dotCount);
			}
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		}  catch (ExpiredJwtException e) {
	        System.err.println("Token validation error: " + e.getMessage());
	        return false;
	    } catch (MalformedJwtException e) {
	        System.err.println("Malformed JWT token: " + e.getMessage());
	        return false;
	    } catch (JwtException | IllegalArgumentException e) {
	        System.err.println("Invalid token: " + e.getMessage());
	        return false;
	    }
		}
}
