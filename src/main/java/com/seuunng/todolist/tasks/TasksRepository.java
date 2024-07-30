package com.seuunng.todolist.tasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seuunng.todolist.lists.ListsEntity;

import jakarta.transaction.Transactional;

@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {
	List<TasksEntity> findByList(ListsEntity list);

	@Transactional
	@Modifying
	@Query("DELETE FROM TasksEntity t WHERE t.list.no = :listNo")
	void deleteByList(@Param("listNo") Long listNo);
}