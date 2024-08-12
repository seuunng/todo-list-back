package com.seuunng.todolist.lists;

import java.sql.Timestamp;
import java.time.Instant;
<<<<<<< HEAD
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.seuunng.todolist.tasks.TasksEntity;
import com.seuunng.todolist.users.UsersEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
=======

import com.seuunng.todolist.users.UsersEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
>>>>>>> origin/server
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
<<<<<<< HEAD
import jakarta.persistence.OneToMany;
=======
>>>>>>> origin/server
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "lists")
public class ListsEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no", nullable = false, updatable = false)
    private Long no;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "icon")
    private String icon;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "user_no")
<<<<<<< HEAD
    @JsonBackReference
    private UsersEntity user;
    
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TasksEntity> tasks;
    
=======
    private UsersEntity user;
    
>>>>>>> origin/server
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
    }

}
