package com.seuunng.todolist.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

<<<<<<< HEAD
import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.ListsRepository;
=======
>>>>>>> origin/server
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksEntityTest {
	@Autowired
    private UsersRepository usersRepository;
	@Autowired
    private TasksRepository tasksRepository;
<<<<<<< HEAD
	@Autowired
    private ListsRepository listsRepository;
	
	@Test
    public void testCreateTask() {

		 String userEmail = "geust@example.com";
        UsersEntity user = usersRepository.findByEmail(userEmail).orElseGet(() -> {
            UsersEntity newUser = new UsersEntity();
            newUser.setEmail("geust@example.com");
            newUser.setNickname("Geust");
            newUser.setPassword("password");
            return usersRepository.save(newUser);
        });

        // Create and save ListsEntity
        ListsEntity listsEntity = new ListsEntity();
        listsEntity.setTitle("List 1");
        listsEntity = listsRepository.save(listsEntity);
=======
	
	@Test
    public void testCreateTask() {
		 String userId = "sampleUser";
        UsersEntity user = usersRepository.findById(userId).orElseGet(() -> {
            UsersEntity newUser = new UsersEntity();
            newUser.setId(userId);
            newUser.setNickname("Test User");
            newUser.setPassword("password");
            return usersRepository.save(newUser);
        });
>>>>>>> origin/server
        
        TasksEntity task = new TasksEntity();
        task.setTitle("Sample today todo");
        task.setContent("오늘 할 일 뭔가요?!");
        task.setStartDate(new Timestamp(System.currentTimeMillis()));
        task.setEndDate(new Timestamp(System.currentTimeMillis() + 100000));
        task.setPriority(TasksEntity.Priority.MEDIUM);
        task.setIsRepeated(TasksEntity.IsRepeated.NOREPEAT);
<<<<<<< HEAD
        task.setIsNotified(TasksEntity.IsNotified.NOALARM);
        task.setTaskStatus(TasksEntity.TaskStatus.PENDING);
        task.setUser(user);
        task.setList(listsEntity);
=======
        task.setIsNotified(TasksEntity.IsNotified.NOALRAM);
        task.setTaskStatus(TasksEntity.TaskStatus.PENDING);
        task.setUser(user);
>>>>>>> origin/server
        
        TasksEntity savedTask = tasksRepository.save(task);

     // 검증
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getNo()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Sample today todo");
        assertThat(savedTask.getContent()).isEqualTo("오늘 할 일 뭔가요?!");
        assertThat(savedTask.getPriority()).isEqualTo(TasksEntity.Priority.MEDIUM);
        assertThat(savedTask.getTaskStatus()).isEqualTo(TasksEntity.TaskStatus.PENDING);
        assertThat(savedTask.getUser()).isEqualTo(user);
    }
}
