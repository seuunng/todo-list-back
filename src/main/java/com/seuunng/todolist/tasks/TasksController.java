package com.seuunng.todolist.tasks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.users.UsersRepository;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TasksController {
	
	@Autowired
    private UsersRepository usersRepository;
	@Autowired
    private TasksRepository tasksRepository;
	
	@GetMapping("/task")
	public List<TasksEntity> getList() {
		List<TasksEntity> tasks = tasksRepository.findAll();
		System.out.println(tasks);
		return tasks;
	}

	@PostMapping("/task")
	public TasksEntity addTask(@RequestBody TasksEntity newTask) {
		TasksEntity task = tasksRepository.save(newTask);
		return task;
	}
}
