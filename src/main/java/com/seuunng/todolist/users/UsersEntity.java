package com.seuunng.todolist.users;

import java.sql.Blob;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UsersEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long no;
	
	@Column(nullable = false, unique = true)
	private String id;
	
    @Column(nullable = false)
	private String nickname;
    
    @Column(nullable = false)
	private String password;
    
//    @Column(nullable = false)
//	private boolean isSimple; //간편 로그인
//	private String simplePassword; //간편 로그인 비밀번호
//    @Lob
//	private Blob figierprint; //지문 로그인
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	private Date created_at;
    
    @PrePersist
    protected void onCreate() {
        if (created_at == null) {
            created_at = new Date();
        }
        if (nickname == null || nickname.isEmpty()) {
            nickname = id; 
        }
    }
}
