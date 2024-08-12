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
@RequestMapping("/lists")
@CrossOrigin(origins = "http://localhost:3000")
public class ListsController {
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private TasksRepository tasksRepository;
	@Autowired
	private ListsRepository listsRepository;

	@GetMapping("/list")
	public List<ListsEntity> getList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
//        System.out.println("authentication "+authentication);
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<ListsEntity> lists = listsRepository.findByUserId(currentUser.getId());
		System.out.println("Lists: " + lists);
		return lists;
	}

	@PostMapping(value = "/list", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addList(@RequestBody ListsEntity newList, Authentication authentication) {
		try {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersEntity user = userDetails.getUser();
			newList.setUser(user);
			
		ListsEntity list = listsRepository.save(newList);
		return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("addList Error: " + e.getMessage());
		}
	}

	@PutMapping("/list/{no}")
	public ResponseEntity<ListsEntity> updateList(@PathVariable("no") Long no, @RequestBody ListsEntity newList) {
		System.out.println("Received PUT request to update task with ID: " + no);
		System.out.println("Request Body: " + newList);

		return listsRepository.findById(no).map(list -> {
			list.setTitle(newList.getTitle());
			list.setIcon(newList.getIcon());
			list.setColor(newList.getColor());
			list.setCreatedAt(newList.getCreatedAt());
			list.setIsDeleted(newList.getIsDeleted());

			listsRepository.save(list);
			System.out.println("Lists updated: " + list);

			return ResponseEntity.ok(list);
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	@DeleteMapping("/list/{no}")
	public ResponseEntity<?> deleteList(@PathVariable("no") Long no) {
		try { tasksRepository.deleteByList(no);
			  listsRepository.deleteById(no);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
