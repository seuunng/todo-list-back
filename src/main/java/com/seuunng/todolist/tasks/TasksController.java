package com.seuunng.todolist.tasks;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.seuunng.todolist.lists.SmartListsEntity;
import com.seuunng.todolist.lists.SmartListsRepository;
import com.seuunng.todolist.login.CustomUserDetails;
import com.seuunng.todolist.login.TaskDTO;
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = {"http://localhost:3000", "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app"})
public class TasksController {
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private TasksRepository tasksRepository;
	@Autowired
	private ListsRepository listsRepository;
	@Autowired
	private SmartListsRepository smartListsRepository;
	@Autowired
	private TasksService tasksService;

	@GetMapping("/task/{id}")
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
		} catch (ResourceNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	@GetMapping("/bySmartList")
	public ResponseEntity<List<TasksEntity>> getTasksBySmartListId(@RequestParam("listId") Long listId) {
		System.out.println("bySmartList 실행");
		try {
			SmartListsEntity list = smartListsRepository.findById(listId)
					.orElseThrow(() -> new ResourceNotFoundException("List not found"));

			List<TasksEntity> tasks = tasksRepository.findBySmartList(list);

			System.out.println("tasks : "+tasks);
			return ResponseEntity.ok(tasks);
		} catch (ResourceNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	@PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addTask(@RequestBody TaskDTO taskDTO, Authentication authentication) {
		try {
	        System.out.println("Received Task: " + taskDTO);
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersEntity user = userDetails.getUser();
			
			 TasksEntity newTask = new TasksEntity();
		        newTask.setTitle(taskDTO.getTitle());
		        newTask.setContent(taskDTO.getContent());
		        newTask.setStartDate(taskDTO.getStartDate());
		        newTask.setEndDate(taskDTO.getEndDate());
		        newTask.setPriority(taskDTO.getPriority());
		        newTask.setDateStatus(taskDTO.getDateStatus());
		        newTask.setIsRepeated(taskDTO.getIsRepeated());
		        newTask.setIsNotified(taskDTO.getIsNotified());
		        newTask.setListNo(taskDTO.getListNo());
		        newTask.setSmartListNo(taskDTO.getSmartListNo());
		        newTask.setTaskStatus(taskDTO.getTaskStatus());
		        newTask.setIsTimeSet(taskDTO.getIsTimeSet());
		        newTask.setUser(user);
		        
			newTask.setUser(user);
			
			if (newTask.getList() == null || newTask.getSmartList() == null) {
				SmartListsEntity defaultList = smartListsRepository.findByUserAndTitle(user, "기본함")
                        .orElseGet(() -> {
                        	SmartListsEntity newList = new SmartListsEntity();
                            newList.setTitle("기본함");
                            newList.setUser(user);
                            newList.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                            newList.setIsDeleted(false);
                            return smartListsRepository.save(newList);
                        });
                newTask.setSmartList(defaultList);
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
			task.setIsTimeSet(newTask.getIsTimeSet());
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
	public ResponseEntity<?> updateTaskStatus(@PathVariable("no") Long no, @RequestBody TaskStatusUpdateRequest request) {
		if (request.getStatus() == null) {
	        return ResponseEntity.badRequest().body("Task status must not be null");
	    }
		tasksService.updateTaskStatus(no, request.getStatus());
		 Map<String, String> response = new HashMap<>();
		    response.put("message", "Task status updated successfully");
		    response.put("taskStatus", request.getStatus().name());
        return ResponseEntity.ok(response);
	}
	
	@GetMapping("/default")
	public List<TasksEntity> getDefaultList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<TasksEntity> defaultTasks = tasksRepository.findByUserIdAndListIsNull(currentUser.getId());
		return defaultTasks;
	}
	
	@GetMapping("/today")
	public List<TasksEntity> getTodayList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Timestamp today = new Timestamp(System.currentTimeMillis());
        
		List<TasksEntity> todayTasks = tasksRepository.findTodayTasks(currentUser.getId(), today);
		return todayTasks;
	}

	@GetMapping("/tomorrow")
	public List<TasksEntity> getTomorrowList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 현재 날짜를 기준으로 오늘의 자정 시간을 가져옵니다. (14일 00:00:00)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp todayStart = new Timestamp(calendar.getTimeInMillis());

        // 내일의 자정 시간 (15일 00:00:00)
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Timestamp tomorrowStart = new Timestamp(calendar.getTimeInMillis());
        
        // 내일의 끝 시간 (모레 자정 전까지)
        // 내일의 자정 전 (15일 23:59:59)
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.add(Calendar.SECOND, -1);
        Timestamp tomorrowEnd = new Timestamp(calendar.getTimeInMillis());
        
        //내일까지 해야할일, 오늘 포함 
		List<TasksEntity> tomorrowTasks = tasksRepository.findTomorrowTasks(currentUser.getId(),  todayStart, tomorrowEnd);
		return tomorrowTasks;
	}

	@GetMapping("/next7Days")
	public List<TasksEntity> getNext7DaysList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
     // 현재 날짜를 기준으로 오늘의 자정 시간을 가져옵니다.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp todayStart = new Timestamp(calendar.getTimeInMillis());

        // 7일 후의 자정 시간 (7일 후의 끝 시간)
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Timestamp next7DaysEnd = new Timestamp(calendar.getTimeInMillis());
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<TasksEntity> next7DaysTasks = tasksRepository.findTasksForNext7Days(currentUser.getId(), todayStart, next7DaysEnd);
        return next7DaysTasks;
	}

	@GetMapping("/completed")
	public List<TasksEntity> getCompletedList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<TasksEntity> completedTasks = tasksRepository.findByCompletedTasks(currentUser.getId());
		return completedTasks;
	}

	@GetMapping("/deleted")
	public List<TasksEntity> getDeletedList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<TasksEntity> deletedTasks = tasksRepository.findDeletedTasks(currentUser.getId());
        return deletedTasks;
	}
}
