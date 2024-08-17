package com.seuunng.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.seuunng.todolist.*"})
@EnableJpaRepositories(basePackages = {"com.seuunng.todolist.*"})

public class TodoListBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoListBackApplication.class, args);
	}

}
