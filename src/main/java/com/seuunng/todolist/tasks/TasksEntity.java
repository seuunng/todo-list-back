package com.seuunng.todolist.tasks;

import java.sql.Timestamp;

import com.seuunng.todolist.users.UsersEntity;

import jakarta.annotation.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class TasksEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no", nullable = false, updatable = false)
    private Long no;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content")
    private String content;
    
    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "priority", columnDefinition = "ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM'")
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "is_repeated")
    private Boolean isRepeated;

    @Column(name = "is_notified")
    private Boolean isNotified;

    @Column(name = "task_status", columnDefinition = "ENUM('COMPLETED', 'PENDING', 'OVERDUE', 'CANCELLED')")
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

//    @ManyToOne
//    @JoinColumn(name = "secsion_no")
//    private SectionEntity section;

//    @ManyToOne
//    @JoinColumn(name = "list_no")
//    private ListEntity list;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private UsersEntity user;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
    }
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum TaskStatus {
        COMPLETED,
        PENDING,
        OVERDUE,
        CANCELLED
    }
}
