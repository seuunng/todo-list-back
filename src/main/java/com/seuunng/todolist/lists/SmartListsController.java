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
@CrossOrigin(origins =  {"http://localhost:3000", "https://web-todolistproject-lzy143lgf0f1c3f8.sel4.cloudtype.app"})
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
        
        UsersEntity currentUser = usersRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
		List<SmartListsEntity> lists = smartListsRepository.findByUserId(currentUser.getId());
		return lists;
	}

	

}
