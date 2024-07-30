package com.seuunng.todolist.tasks;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    public List<Task> getTasks() {
    	 return tasksRepository.findAll().stream()
                 .map(taskEntity -> new Task(taskEntity.getNo(), taskEntity.getTitle(), taskEntity.getContent()))
                 .collect(Collectors.toList());
    }
}
