package com.seuunng.todolist.config;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.ListsRepository;
import com.seuunng.todolist.lists.SmartListsEntity;
import com.seuunng.todolist.lists.SmartListsRepository;
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UsersRepository usersRepository, ListsRepository listsRepository, SmartListsRepository smartListsRepository, PasswordEncoder passwordEncoder) {
    	return args -> {
            if (!usersRepository.existsByEmail("guest@gmail.com")) {
                UsersEntity user = new UsersEntity();
                user.setEmail("guest@gmail.com");
                user.setPassword(passwordEncoder.encode("guest123")); // 암호는 실제 사용 시 강력하게 설정하세요.
                user.setNickname("게스트");
                usersRepository.save(user);

                ListsEntity defaultList = new ListsEntity();
                defaultList.setTitle("기본함");
                defaultList.setUser(user);
//                defaultList.setIcon("default-icon"); // 적절한 아이콘 설정
//                defaultList.setColor("#FFFFFF"); // 적절한 색상 설정
                defaultList.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList.setIsDeleted(false);
                listsRepository.save(defaultList);
                

                SmartListsEntity defaultList_basic = new SmartListsEntity();
                defaultList_basic.setTitle("모든 할 일");
                defaultList_basic.setIcon("FiInbox");
                defaultList_basic.setUser(user);
                defaultList_basic.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_basic.setIsDeleted(false);
                smartListsRepository.save(defaultList_basic);

                SmartListsEntity defaultList_today = new SmartListsEntity();
                defaultList_today.setTitle("오늘 할 일");
                defaultList_today.setIcon("FaRegCalendarCheck");
                defaultList_today.setUser(user);
                defaultList_today.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_today.setIsDeleted(false);
                smartListsRepository.save(defaultList_today);
                
                SmartListsEntity defaultList_tommorow = new SmartListsEntity();
                defaultList_tommorow.setTitle("내일 할 일");
                defaultList_tommorow.setIcon("FiSunrise"); 
                defaultList_tommorow.setUser(user);
                defaultList_tommorow.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_tommorow.setIsDeleted(false);
                smartListsRepository.save(defaultList_tommorow);

                SmartListsEntity defaultList_nextWeek = new SmartListsEntity();
                defaultList_nextWeek.setTitle("다음주 할 일");
                defaultList_nextWeek.setIcon("BsCalendarWeek"); 
                defaultList_nextWeek.setUser(user);
                defaultList_nextWeek.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_nextWeek.setIsDeleted(false);
                smartListsRepository.save(defaultList_nextWeek);
                
                SmartListsEntity defaultList_completed = new SmartListsEntity();
                defaultList_completed.setTitle("완료한 할 일");
                defaultList_completed.setIcon("FaCheck");
                defaultList_completed.setUser(user);
                defaultList_completed.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_completed.setIsDeleted(false);
                smartListsRepository.save(defaultList_completed);

                SmartListsEntity defaultList_deleted = new SmartListsEntity();
                defaultList_deleted.setTitle("휴지통");
                defaultList_deleted.setIcon("IoClose");
                defaultList_deleted.setUser(user);
                defaultList_deleted.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_deleted.setIsDeleted(false);
                smartListsRepository.save(defaultList_deleted);
            }
            if (!usersRepository.existsByEmail("mnb2856@gmail.com")) {
                UsersEntity user = new UsersEntity();
                user.setEmail("mnb2856@gmail.com");
                user.setPassword(passwordEncoder.encode("")); // 암호는 실제 사용 시 강력하게 설정하세요.
                user.setNickname("승희");
                usersRepository.save(user);

                ListsEntity defaultList = new ListsEntity();
                defaultList.setTitle("기본함");
                defaultList.setUser(user);
                defaultList.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList.setIsDeleted(false);
                listsRepository.save(defaultList);
                
                SmartListsEntity defaultList_basic = new SmartListsEntity();
                defaultList_basic.setTitle("모든 할 일");
                defaultList_basic.setIcon("FiInbox");
                defaultList_basic.setUser(user);
                defaultList_basic.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_basic.setIsDeleted(false);
                smartListsRepository.save(defaultList_basic);

                SmartListsEntity defaultList_today = new SmartListsEntity();
                defaultList_today.setTitle("오늘 할 일");
                defaultList_today.setIcon("FaRegCalendarCheck");
                defaultList_today.setUser(user);
                defaultList_today.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_today.setIsDeleted(false);
                smartListsRepository.save(defaultList_today);
                
                SmartListsEntity defaultList_tommorow = new SmartListsEntity();
                defaultList_tommorow.setTitle("내일 할 일");
                defaultList_tommorow.setIcon("FiSunrise"); 
                defaultList_tommorow.setUser(user);
                defaultList_tommorow.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_tommorow.setIsDeleted(false);
                smartListsRepository.save(defaultList_tommorow);

                SmartListsEntity defaultList_nextWeek = new SmartListsEntity();
                defaultList_nextWeek.setTitle("다음주 할 일");
                defaultList_nextWeek.setIcon("BsCalendarWeek"); 
                defaultList_nextWeek.setUser(user);
                defaultList_nextWeek.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_nextWeek.setIsDeleted(false);
                smartListsRepository.save(defaultList_nextWeek);
                
                SmartListsEntity defaultList_completed = new SmartListsEntity();
                defaultList_completed.setTitle("완료한 할 일");
                defaultList_completed.setIcon("FaCheck");
                defaultList_completed.setUser(user);
                defaultList_completed.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_completed.setIsDeleted(false);
                smartListsRepository.save(defaultList_completed);

                SmartListsEntity defaultList_deleted = new SmartListsEntity();
                defaultList_deleted.setTitle("취소한 할 일");
                defaultList_deleted.setIcon("IoClose");
                defaultList_deleted.setUser(user);
                defaultList_deleted.setCreatedAt(Timestamp.from(Instant.now())); // 적절한 생성일 설정
                defaultList_deleted.setIsDeleted(false);
                smartListsRepository.save(defaultList_deleted);
            }
        };
    }
}
