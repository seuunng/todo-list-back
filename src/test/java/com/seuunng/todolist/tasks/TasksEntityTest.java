package com.seuunng.todolist.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksEntityTest {
	@Autowired
    private UsersRepository usersRepository;
	@Autowired
    private TasksRepository tasksRepository;
	
	@Test
    public void testCreateTask() {
		UsersEntity user = new UsersEntity();
        user.setId("Sample user");
        user.setNickname("Test User");
        user.setPassword("password");
        
        UsersEntity savedUser = usersRepository.save(user);
        
        TasksEntity task = new TasksEntity();
        task.setTitle("Sample today todo");
        task.setContent("오늘 할 일 뭔가요?!");
        task.setStartDate(new Timestamp(System.currentTimeMillis()));
        task.setEndDate(new Timestamp(System.currentTimeMillis() + 100000));
        task.setPriority(TasksEntity.Priority.MEDIUM);
        task.setIsRepeated(TasksEntity.IsRepeated.NOREPEAT);
        task.setIsNotified(TasksEntity.IsNotified.NOALRAM);
        task.setTaskStatus(TasksEntity.TaskStatus.PENDING);
        task.setUser(savedUser);
        
        TasksEntity savedTask = tasksRepository.save(task);

     // 검증
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getNo()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Sample today todo");
        assertThat(savedTask.getContent()).isEqualTo("오늘 할 일 뭔가요?!");
        assertThat(savedTask.getPriority()).isEqualTo(TasksEntity.Priority.MEDIUM);
        assertThat(savedTask.getTaskStatus()).isEqualTo(TasksEntity.TaskStatus.PENDING);
        assertThat(savedTask.getUser()).isEqualTo(savedUser);
    }
}
