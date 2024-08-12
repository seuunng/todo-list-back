package com.seuunng.todolist.tasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
=======
>>>>>>> origin/server
import org.springframework.stereotype.Repository;

import com.seuunng.todolist.lists.ListsEntity;

<<<<<<< HEAD
import jakarta.transaction.Transactional;

@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {
	List<TasksEntity> findByList(ListsEntity list);

	@Transactional
	@Modifying
	@Query("DELETE FROM TasksEntity t WHERE t.list.no = :listNo")
	void deleteByList(@Param("listNo") Long listNo);
	
	@Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId")
    List<TasksEntity> findByUserId(@Param("userId") Long userId);
	
	@Query("SELECT t FROM TasksEntity t JOIN FETCH t.list WHERE t.list.no = :listId")
    List<TasksEntity> findAllByListId(@Param("listId") Long listId);
}
=======
@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {
	 List<TasksEntity> findByList(ListsEntity list);
}
>>>>>>> origin/server
