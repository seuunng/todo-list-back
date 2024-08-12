package com.seuunng.todolist.lists;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seuunng.todolist.users.UsersEntity;
@Repository
public interface SmartListsRepository extends JpaRepository<SmartListsEntity, Long>{
	
	Optional<SmartListsEntity> findByUserAndTitle(UsersEntity user, String title);
	
	List<SmartListsEntity> findByUserId(Long userId);
	
	@Query("SELECT l FROM ListsEntity l JOIN FETCH l.tasks WHERE l.id = :id")
	SmartListsEntity findByIdWithTasks(@Param("id") Long id);
}
