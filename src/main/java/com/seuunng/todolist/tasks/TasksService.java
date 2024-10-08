package com.seuunng.todolist.tasks;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seuunng.todolist.tasks.TasksEntity.TaskStatus;

@Service
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    public List<Task> getTasks() {
    	 return tasksRepository.findAll().stream()
                 .map(taskEntity -> new Task(taskEntity.getNo(), taskEntity.getTitle(), taskEntity.getContent()))
                 .collect(Collectors.toList());
    }
    

    public List<TasksEntity> getTasksByUserId(Long id) {
        System.out.println("Fetching tasks for user ID: " + id); 
        List<TasksEntity> tasks = tasksRepository.findByUserId(id);
        System.out.println("Tasks fetched: " + tasks); // 디버깅용 출력
        return tasks;
    }
    
    public void updateTaskStatus(Long id, TaskStatus newStatus) {
        TasksEntity task = tasksRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setTaskStatus(newStatus);
        tasksRepository.save(task);
    }

}
