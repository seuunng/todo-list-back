package com.seuunng.todolist.tasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seuunng.todolist.lists.ListsEntity;

@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {
	 List<TasksEntity> findByList(ListsEntity list);
}
