package com.seuunng.todolist.users;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
	@Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersService userService;
    @GetMapping
    public List<UsersEntity> getUsers() {
        return usersRepository.findAll();
    }
    @GetMapping("/user")
    public UsersEntity getUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
    	  if (oAuth2User == null) {
              throw new RuntimeException("Authenticated user is not available");
          }
    	  Map<String, Object> userAttributes = oAuth2User.getAttributes();
          String email = (String) userAttributes.get("email");
          
          return usersRepository.findByEmail(email)
                  .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
     }
}
