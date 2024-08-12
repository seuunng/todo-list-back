package com.seuunng.todolist.lists;

import java.sql.Timestamp;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.seuunng.todolist.login.ListDTO;
import com.seuunng.todolist.login.TaskDTO;
import com.seuunng.todolist.tasks.ResourceNotFoundException;

public class ListsService {
	 @Autowired
	    private ListsRepository listRepository;

	    public ListDTO getList(Long id) {
	        ListsEntity listsEntity = listRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("List not found"));
	        ListDTO listDTO = new ListDTO();
	        listDTO.setNo(listsEntity.getNo());
	        listDTO.setTitle(listsEntity.getTitle());
	        listDTO.setIcon(listsEntity.getIcon());
	        listDTO.setColor(listsEntity.getColor());
	        listDTO.setCreatedAt(listsEntity.getCreatedAt());
	        listDTO.setIsDeleted(listsEntity.getIsDeleted());
	        listDTO.setTasks(listsEntity.getTasks().stream().map(task -> {
	            TaskDTO taskDTO = new TaskDTO();
	            taskDTO.setNo(task.getNo());
	            taskDTO.setTitle(task.getTitle());
	            taskDTO.setContent(task.getContent());
	            taskDTO.setStartDate(task.getStartDate());
	            taskDTO.setEndDate(task.getEndDate());
	            taskDTO.setPriority(task.getPriority());
	            taskDTO.setDateStatus(task.getDateStatus());
	            taskDTO.setIsRepeated(task.getIsRepeated());
	            taskDTO.setIsNotified(task.getIsNotified());
	            taskDTO.setTaskStatus(task.getTaskStatus());
	            taskDTO.setCreatedAt(task.getCreatedAt());
	            return taskDTO;
	        }).collect(Collectors.toList()));
	        return listDTO;
	    }

}
