package com.seuunng.todolist.login;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class SessionController {

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {

    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("3 Authentication: " + authentication.getPrincipal());

    	
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            System.out.println("User is authenticated: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("Authentication: " + authentication);
            System.out.println("Authentication Principal: " + authentication.getPrincipal());
            System.out.println("Is Authenticated: " + authentication.isAuthenticated());
            
            UsersEntity user = (UsersEntity) request.getSession().getAttribute("user");
            if (user != null) {
                return ResponseEntity.ok(new AuthResponse(user));
            } else {
                return ResponseEntity.status(401).body("세션이 유효하지 않습니다.");
            }
        } else {
            System.err.println("User is not authenticated");
            return ResponseEntity.status(401).body("사용자가 인증되지 않았습니다.");
        }
    }
}