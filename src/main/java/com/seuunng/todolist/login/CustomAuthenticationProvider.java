package com.seuunng.todolist.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String email = authentication.getName();
		String password = (String)authentication.getCredentials();
		
        log.debug("Authenticating user with email: {}", email);
		
		AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(email);

        log.debug("Loaded user: {}", accountContext.getAccount().getEmail());
        
		if (!passwordEncoder.matches(password, accountContext.getPassword())) {
	        System.out.println("Authentication failed for email: " + email);
			throw new BadCredentialsException("BadCredentialsException'");
		}
		
		return new UsernamePasswordAuthenticationToken(
				accountContext,
				null,
				accountContext.getAuthorities()
			);
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
