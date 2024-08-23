package com.seuunng.todolist.users;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.lists.ListsEntity;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app"})
public class UsersController {
	@Autowired
    private UsersRepository usersRepository;
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
    @PutMapping("/user/{no}")
    public  ResponseEntity<UsersEntity> updateUser(@PathVariable("no") Long no, @RequestBody UsersEntity updateUser) {
    	
    	return usersRepository.findById(no).map(user -> {
    		user.setMainListNo(updateUser.getMainListNo());
	
			usersRepository.save(user);

			return ResponseEntity.ok(user);
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}
}
