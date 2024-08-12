package com.seuunng.todolist.tasks;

<<<<<<< HEAD
=======
import static org.assertj.core.api.Assertions.assertThat;
>>>>>>> origin/server
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.seuunng.todolist.users.UsersRepository;
import com.seuunng.todolist.lists.ListsRepository;
=======
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.seuunng.todolist.users.UsersRepository;

>>>>>>> origin/server

@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TasksRepository tasksRepository;
    @MockBean
    private UsersRepository usersRepository;
<<<<<<< HEAD
    @MockBean
    private ListsRepository listsRepository;
    @MockBean
    private TasksService tasksService; 

    @Test
    @WithMockUser(username = "Guest", roles = {"ADMIN"})
    public void testGetList() throws Exception {
        Long userId = 1L; // 예제 사용자 번호
        mockMvc.perform(get("/tasks/task/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    // 여기서 실제 응답과 기대하는 응답을 비교합니다.
                    // assertThat(responseString).contains("expectedContent");
                });
=======
    @Test
    public void testGetList() throws Exception {
        mockMvc.perform(get("/tasks/task"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isNotEmpty());
>>>>>>> origin/server
    }
}