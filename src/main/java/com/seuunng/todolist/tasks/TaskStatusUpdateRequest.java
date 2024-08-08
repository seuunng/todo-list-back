package com.seuunng.todolist.tasks;

import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
	private TasksEntity.TaskStatus status;
}
