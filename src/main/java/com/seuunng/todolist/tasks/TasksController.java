package com.seuunng.todolist.tasks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.ListsRepository;
import com.seuunng.todolist.users.UsersRepository;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TasksController {
	
	@Autowired
    private UsersRepository usersRepository;
	@Autowired
    private TasksRepository tasksRepository;
	@Autowired
    private ListsRepository listsRepository;
	
	@GetMapping("/task")
	public List<TasksEntity> getList() {
		List<TasksEntity> tasks = tasksRepository.findAll();
		System.out.println(tasks);
		return tasks;
	}
	 @GetMapping("/byList")
	  public ResponseEntity<List<TasksEntity>> getTasksByListId(@RequestParam("listId") Long listId) {
		 try {
	            ListsEntity list = listsRepository.findById(listId)
	                .orElseThrow(() -> new ResourceNotFoundException("List not found"));
	            
	            List<TasksEntity> tasks = tasksRepository.findByList(list);
	            return ResponseEntity.ok(tasks);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
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
	        task.setDateStatus(newTask.getDateStatus());
			task.setIsRepeated(newTask.getIsRepeated());
			task.setIsNotified(newTask.getIsNotified());
			task.setTaskStatus(newTask.getTaskStatus());
			if (newTask.getList() != null) {
                ListsEntity list = listsRepository.findById(newTask.getList().getNo())
                    .orElseThrow(() -> new ResourceNotFoundException("List not found"));
                task.setList(list);
            }
			
			tasksRepository.save(task);
            System.out.println("Task updated: " + task);
			
			return  ResponseEntity.ok(task);
		})
		.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}
	
	@DeleteMapping("/task/{no}")
	 public ResponseEntity<?> deleteTask(@PathVariable("no") Long no) {
		if (no == null) {
           return ResponseEntity.badRequest().body("Task no cannot be null");
       }
      tasksRepository.deleteById(no);
      return ResponseEntity.ok("task deleted successfully");
  }
}
