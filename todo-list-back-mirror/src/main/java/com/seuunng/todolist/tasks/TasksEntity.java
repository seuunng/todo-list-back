package com.seuunng.todolist.tasks;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.lists.SmartListsEntity;
import com.seuunng.todolist.users.UsersEntity;

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
    
    @Column(name = "date_status", columnDefinition = "ENUM('DATE', 'PERIOD') DEFAULT 'DATE'")
    @Enumerated(EnumType.STRING)
    private  DateStatus dateStatus = DateStatus.DATE;
    
    @Column(name = "is_repeated", columnDefinition = "ENUM('NOREPEAT', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')")
    @Enumerated(EnumType.STRING)
    private IsRepeated isRepeated = IsRepeated.NOREPEAT;

    @Column(name = "is_notified", columnDefinition = "ENUM('NOALARM', 'ONTIME', 'FIVEMINS', 'THIRTYMINS', 'DAYEARLY')")
    @Enumerated(EnumType.STRING)
    private IsNotified isNotified = IsNotified.NOALARM;

    @Column(name = "task_status", columnDefinition = "ENUM('COMPLETED', 'PENDING', 'OVERDUE', 'CANCELLED')")
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

//    @ManyToOne
//    @JoinColumn(name = "secsion_no")
//    private SectionEntity section;

    @ManyToOne
    @JoinColumn(name = "list_no")
    @JsonBackReference
    private ListsEntity list;
    
    public Long getListNo() {
        return list != null ? list.getNo() : null;
    }
    @ManyToOne
    @JoinColumn(name = "smart_list_no")
    @JsonBackReference
    private SmartListsEntity smartList;
    
    public Long getSmartListNo() {
        return smartList != null ? smartList.getNo() : null;
    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore

    private UsersEntity user;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
    }
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum TaskStatus {
        COMPLETED, PENDING, OVERDUE, CANCELLED
    }
    
    public enum DateStatus {
        DATE, PERIOD
    }
    public enum IsRepeated {
    	NOREPEAT, DAILY, WEEKLY, MONTHLY, YEARLY
    }
    
    public enum IsNotified {

    	NOALARM, ONTIME, FIVEMINS, THIRTYMINS, DAYEARLY

    }
}
