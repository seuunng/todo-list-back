package com.seuunng.todolist.login;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oauth2")
public class OAuthController implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository usersRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    public OAuthController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        UsersEntity user = usersRepository.findByEmail(email).orElseGet(() -> {
            UsersEntity newUser = new UsersEntity();
            newUser.setEmail(email);
            newUser.setNickname(name);
            newUser.setPassword(""); 
            return usersRepository.save(newUser);
        });
     // JWT 생성
        String token = jwtTokenProvider.generateToken(email, user.getRoles());

        // 응답으로 리다이렉션
        HttpServletResponse response = (HttpServletResponse) userRequest.getAdditionalParameters().get(HttpServletResponse.class.getName());
        String redirectUrl = "http://localhost:3000/loginSuccess?token=" + token;
        return new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), attributes, "sub");
    }
}
