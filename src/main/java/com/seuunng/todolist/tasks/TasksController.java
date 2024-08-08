package com.seuunng.todolist.tasks;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.seuunng.todolist.login.CustomUserDetails;
import com.seuunng.todolist.users.UsersEntity;
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
	@Autowired
	private TasksService tasksService;

	@GetMapping("/task/{id}")
//	@PreAuthorize("hasRole('USER')")
	public List<TasksEntity> getTasksByUserId(@PathVariable("id") Long id) {
		List<TasksEntity> tasks = tasksService.getTasksByUserId(id);
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

	@PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addTask(@RequestBody TasksEntity newTask, Authentication authentication) {
		try {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersEntity user = userDetails.getUser();
			newTask.setUser(user);
			
			if (newTask.getList() == null || newTask.getList().getNo() == null) {
                ListsEntity defaultList = listsRepository.findByUserAndTitle(user, "기본함")
                        .orElseGet(() -> {
                            ListsEntity newList = new ListsEntity();
                            newList.setTitle("기본함");
                            newList.setUser(user);
                            newList.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                            newList.setIsDeleted(false);
                            return listsRepository.save(newList);
                        });
                newTask.setList(defaultList);
            } else {
                ListsEntity list = listsRepository.findById(newTask.getList().getNo())
                        .orElseThrow(() -> new ResourceNotFoundException("List not found"));
                newTask.setList(list);
            }
			
			TasksEntity task = tasksRepository.save(newTask);
			return ResponseEntity.ok(task);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("addTask Error: " + e.getMessage());
		}
	}

	@PutMapping("/task/{no}")
	public ResponseEntity<TasksEntity> updateTask(@PathVariable("no") Long no, @RequestBody TasksEntity newTask) {
		System.out.println("Received PUT request to update task with ID: " + no);
		System.out.println("Request Body: " + newTask);

		return tasksRepository.findById(no).map(task -> {
			task.setTitle(newTask.getTitle());
			task.setContent(newTask.getContent());
			task.setStartDate(newTask.getStartDate());
			task.setEndDate(newTask.getEndDate());
			task.setPriority(newTask.getPriority());
			task.setDateStatus(newTask.getDateStatus());
			task.setIsRepeated(newTask.getIsRepeated());
			task.setIsNotified(newTask.getIsNotified());
			task.setTaskStatus(newTask.getTaskStatus());
			task.setTaskStatus(newTask.getTaskStatus());
			if (newTask.getList() != null) {
				ListsEntity list = listsRepository.findById(newTask.getList().getNo())
						.orElseThrow(() -> new ResourceNotFoundException("List not found"));
				task.setList(list);
			}

			tasksRepository.save(task);
			System.out.println("Task updated: " + task);

			return ResponseEntity.ok(task);
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	@DeleteMapping("/task/{no}")
	public ResponseEntity<?> deleteTask(@PathVariable("no") Long no) {
		if (no == null) {
			return ResponseEntity.badRequest().body("Task no cannot be null");
		}
		tasksRepository.deleteById(no);
		return ResponseEntity.ok("task deleted successfully");
	}
	
	@PutMapping("/{no}/status")
	public ResponseEntity<?> updateTaskStatus(@PathVariable("no") Long no) {
		tasksService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
	}
}
