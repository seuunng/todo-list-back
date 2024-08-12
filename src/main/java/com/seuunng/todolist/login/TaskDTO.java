package com.seuunng.todolist.login;

import java.sql.Timestamp;

import com.seuunng.todolist.tasks.TasksEntity.DateStatus;
import com.seuunng.todolist.tasks.TasksEntity.IsNotified;
import com.seuunng.todolist.tasks.TasksEntity.IsRepeated;
import com.seuunng.todolist.tasks.TasksEntity.Priority;
import com.seuunng.todolist.tasks.TasksEntity.TaskStatus;

import lombok.Data;

@Data
public class TaskDTO {
	private Long no;
    private String title;
    private String content;
    private Timestamp startDate;
    private Timestamp endDate;
    private Priority priority;
    private DateStatus dateStatus;
    private IsRepeated isRepeated;
    private IsNotified isNotified;
    private TaskStatus taskStatus;
    private Timestamp createdAt;
    
    // 필요한 다른 필드들
}
