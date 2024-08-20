package com.seuunng.todolist.tasks;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.SmartListsEntity;

import jakarta.transaction.Transactional;

@Repository
public interface TasksRepository extends JpaRepository<TasksEntity, Long> {
	List<TasksEntity> findByList(ListsEntity list);
	List<TasksEntity> findBySmartList(SmartListsEntity list);

	@Transactional
	@Modifying
	@Query("DELETE FROM TasksEntity t WHERE t.list.no = :listNo")
	void deleteByListId(@Param("listNo") Long listNo);
	
	@Transactional
	@Modifying
	@Query("UPDATE TasksEntity t SET t.taskStatus = 'DELETED' WHERE t.list.no = :listNo")
	void markTasksAsDeletedByListId(@Param("listNo") Long listNo);
	
	@Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId")
    List<TasksEntity> findByUserId(@Param("userId") Long user);
	
//	@Query("SELECT t FROM TasksEntity t JOIN FETCH t.list WHERE t.list.no = :listId")
//    List<TasksEntity> findAllByListId(@Param("listId") Long listId);

    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " +
            "(t.taskStatus = 'PENDING' OR t.taskStatus = 'OVERDUE')")
    List<TasksEntity> findByUserIdAndListIsNull(@Param("userId")Long userId);
    
    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " +
            "((t.endDate IS NULL AND t.startDate <= :today) OR " +
            "(t.endDate <= :today)) AND " +
            "(t.taskStatus = 'PENDING' OR t.taskStatus = 'OVERDUE')")
     List<TasksEntity> findTodayTasks(@Param("userId") Long userId, @Param("today") Timestamp today);
    

    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " + 
            "((t.startDate >= :todayStart AND t.startDate <= :tomorrowEnd) OR " +
            "(t.endDate >= :todayStart AND t.endDate <= :tomorrowEnd) OR " +
            "(t.startDate <= :todayStart AND t.endDate >= :tomorrowEnd)) AND " +
            "t.taskStatus IN ('PENDING', 'OVERDUE')")
     List<TasksEntity> findTomorrowTasks
     		(@Param("userId") Long userId, 
     		 @Param("todayStart") Timestamp todayStart, 
    		 @Param("tomorrowEnd") Timestamp tomorrowEnd);
    
    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " +
		    "((t.startDate >= :todayStart AND t.startDate <= :next7DaysEnd) OR" +
		    "(t.endDate >= :todayStart AND t.endDate <= :next7DaysEnd) OR " +
		    "(t.startDate <= :todayStart AND t.endDate >= :next7DaysEnd)) AND " +
		    "t.taskStatus IN ('PENDING', 'OVERDUE')")
    List<TasksEntity> findTasksForNext7Days(@Param("userId") Long userId, @Param("todayStart") Timestamp todayStart, @Param("next7DaysEnd") Timestamp next7DaysEnd);
    
    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " +
		    "t.taskStatus IN ('COMPLETED')")
    List<TasksEntity> findByCompletedTasks(@Param("userId") Long userId);
    
    @Query("SELECT t FROM TasksEntity t WHERE t.user.id = :userId AND " +
    		" t.taskStatus = 'CANCELLED'")
    List<TasksEntity> findDeletedTasks(@Param("userId") Long userId);

}


 