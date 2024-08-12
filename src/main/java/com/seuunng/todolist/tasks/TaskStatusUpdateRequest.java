package com.seuunng.todolist.tasks;

import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
	private TasksEntity.TaskStatus status;
	 // Getter and Setter
    public TasksEntity.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TasksEntity.TaskStatus status) {
        this.status = status;
    }
}
