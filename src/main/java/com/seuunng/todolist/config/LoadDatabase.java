package com.seuunng.todolist.config;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.ListsRepository;
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UsersRepository usersRepository, ListsRepository listsRepository, PasswordEncoder passwordEncoder) {
    	return args -> {
            if (!usersRepository.existsByEmail("11@11")) {
                UsersEntity user = new UsersEntity();
                user.setEmail("11@11");
                user.setPassword(passwordEncoder.encode("1111")); // 암호는 실제 사용 시 강력하게 설정하세요.
                user.setNickname("승희네");
                usersRepository.save(user);

                ListsEntity defaultList = new ListsEntity();
                defaultList.setTitle("기본함");
                defaultList.setUser(user);
//                defaultList.setIcon("default-icon"); // 적절한 아이콘 설정
//                defaultList.setColor("#FFFFFF"); // 적절한 색상 설정
                defaultList.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList.setIsDeleted(false);
                listsRepository.save(defaultList);
            }
        };
    }
}
