package com.seuunng.todolist.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersEntityTest {
	@Autowired
    private UsersRepository usersRepository;

    @Test
    public void testCreateUser() {
<<<<<<< HEAD
    	String userEmail = "guest@example.com";
    	 
    	UsersEntity existingUser = usersRepository.findByEmail(userEmail).orElse(null);
        if (existingUser == null) {
        	 UsersEntity user = new UsersEntity();
             user.setNickname("guest");
             user.setEmail("guest@example.com");
             user.setPassword("password");
             user.setRole("ADMIN");
=======
    	String userId = "testuser";
    	 
    	UsersEntity existingUser = usersRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            UsersEntity user = new UsersEntity();
            user.setId(userId);
            user.setNickname("Test User");
            user.setPassword("password");
>>>>>>> origin/server

        UsersEntity savedUser = usersRepository.save(user);

        assertThat(savedUser).isNotNull();
<<<<<<< HEAD
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("guest@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("guest");
        } else {
            // User already exists, handle accordingly
            System.out.println("User already exists: " + userEmail);
=======
        assertThat(savedUser.getNo()).isNotNull();
        assertThat(savedUser.getId()).isEqualTo("testuser");
        assertThat(savedUser.getNickname()).isEqualTo("Test User");
        } else {
            // User already exists, handle accordingly
            System.out.println("User already exists: " + userId);
>>>>>>> origin/server
        }
    }
}
    