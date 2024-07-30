package com.seuunng.todolist.users;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "users")
public class UsersEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	private String nickname;
    
    @Column(nullable = false)
	private String password;
//    @Column(nullable = false)
//	private boolean isSimple; //간편 로그인
//	private String simplePassword; //간편 로그인 비밀번호
//    @Lob
//	private Blob figierprint; //지문 로그인

//	@Enumerated(EnumType.STRING)
//	@JsonIgnore
//	private PublicStatus publicStatus;
//
//	@JsonIgnore
//	@Enumerated(EnumType.STRING)
//	private ShareStatus shareStatus;
	
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	private Date created_at;
    
    private String role;
    

    public UsersEntity() {
    }
    
    @PrePersist
    protected void onCreate() {
        if (created_at == null) {
            created_at = new Date();
        }
        if (nickname == null || nickname.isEmpty()) {
            nickname = email; 
        }
    }
}
