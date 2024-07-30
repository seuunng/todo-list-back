package com.seuunng.todolist.tasks;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.seuunng.todolist.users.UsersRepository;
import com.seuunng.todolist.lists.ListsRepository;
@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TasksRepository tasksRepository;
    @MockBean
    private UsersRepository usersRepository;
    @MockBean
    private ListsRepository listsRepository;
    @MockBean
    private TasksService tasksService;  // TasksService 추가

    @Test
    @WithMockUser
    public void testGetList() throws Exception {
        mockMvc.perform(get("/tasks/task"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    // 여기서 실제 응답과 기대하는 응답을 비교합니다.
                    // assertThat(responseString).contains("expectedContent");
                });
    }
}