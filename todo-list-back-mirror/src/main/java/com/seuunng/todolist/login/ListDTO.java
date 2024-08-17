package com.seuunng.todolist.login;

import java.sql.Timestamp;
import java.util.List;

import com.seuunng.todolist.users.UsersEntity;

import lombok.Data;

@Data
public class ListDTO {
    private Long no;
    private String title;
    private String icon;
    private String color;
    private Timestamp createdAt;
    private Boolean isDeleted;
    private List<TaskDTO> tasks;
    private UsersEntity user;
}

