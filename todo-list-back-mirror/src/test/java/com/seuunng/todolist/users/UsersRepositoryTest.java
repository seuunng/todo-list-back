package com.seuunng.todolist.users;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 임베디드 데이터베이스 사용 방지
public class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @PersistenceContext
    private EntityManager entityManager;

//    @BeforeEach
//    @Transactional
//    public void setUp() {
//
//        usersRepository.deleteAll();
//        entityManager.flush();
//    }
 
    @Test
    @Rollback(false)
    public void whenFindByEmail_thenReturnUser() {
        // Given
        String email = "11@11";
//        UsersEntity user = new UsersEntity();
//        user.setEmail(email);
//        user.setPassword("testpassword");
//        user.setNickname("testnickname");
//        user.setRole("ROLE_USER");
//        usersRepository.save(user);

        // When
        Optional<UsersEntity> foundUser = usersRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
    }
}
