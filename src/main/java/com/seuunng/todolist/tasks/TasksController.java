package com.seuunng.todolist.tasks;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.tasks.TasksEntity.Priority;
import com.seuunng.todolist.tasks.TasksEntity.TaskStatus;
import com.seuunng.todolist.users.UsersRepository;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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
	
	@PutMapping("/task/{no}")
	public  ResponseEntity<TasksEntity> updateTask(@PathVariable("no") Long no, @RequestBody TasksEntity newTask) {
		System.out.println("Received PUT request to update task with ID: " + no);
        System.out.println("Request Body: " + newTask);
        
		return tasksRepository.findById(no)
		.map(task ->{
			task.setTitle(newTask.getTitle());
			task.setContent(newTask.getContent());
			task.setStartDate(newTask.getStartDate());
			task.setEndDate(newTask.getEndDate());
			task.setPriority(newTask.getPriority());
			task.setIsRepeated(newTask.getIsRepeated());
			task.setIsNotified(newTask.getIsNotified());
			task.setTaskStatus(newTask.getTaskStatus());
			
			tasksRepository.save(task);
            System.out.println("Task updated: " + task);
			
			return  ResponseEntity.ok(task);
		})
		.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}
	
}
