package com.seuunng.todolist.lists;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.seuunng.todolist.login.CustomUserDetails;
import com.seuunng.todolist.tasks.TasksRepository;
import com.seuunng.todolist.users.UsersEntity;
import com.seuunng.todolist.users.UsersRepository;

@RestController
@RequestMapping("/smartLists")
@CrossOrigin(origins = "http://localhost:3000")
public class SmartListsController {
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private TasksRepository tasksRepository;
	@Autowired
	private SmartListsRepository smartListsRepository;

	@GetMapping("/list")
	public List<SmartListsEntity> getList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
//        System.out.println("authentication "+authentication);
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<SmartListsEntity> lists = smartListsRepository.findByUserId(currentUser.getId());
//		System.out.println("Lists: " + lists);
		return lists;
	}

	@PostMapping(value = "/list", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addList(@RequestBody SmartListsEntity newList, Authentication authentication) {
		try {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersEntity user = userDetails.getUser();
			newList.setUser(user);
			
			SmartListsEntity list = smartListsRepository.save(newList);
		return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("addList Error: " + e.getMessage());
		}
	}

	@PutMapping("/list/{no}")
	public ResponseEntity<SmartListsEntity> updateList(@PathVariable("no") Long no, @RequestBody SmartListsEntity newList) {
		System.out.println("Received PUT request to update task with ID: " + no);
		System.out.println("Request Body: " + newList);

		return smartListsRepository.findById(no).map(list -> {
			list.setTitle(newList.getTitle());
			list.setIcon(newList.getIcon());
			list.setColor(newList.getColor());
			list.setCreatedAt(newList.getCreatedAt());
			list.setIsDeleted(newList.getIsDeleted());

			smartListsRepository.save(list);
			System.out.println("Lists updated: " + list);

			return ResponseEntity.ok(list);
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	@DeleteMapping("/list/{no}")
	public ResponseEntity<?> deleteList(@PathVariable("no") Long no) {
		try { tasksRepository.deleteByList(no);
			smartListsRepository.deleteById(no);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
