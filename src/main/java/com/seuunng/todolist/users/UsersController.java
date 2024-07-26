package com.seuunng.todolist.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {
	@Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public List<UsersEntity> getUsers() {
        return usersRepository.findAll();
    }
}
