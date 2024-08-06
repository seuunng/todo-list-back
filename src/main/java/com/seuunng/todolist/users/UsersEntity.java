package com.seuunng.todolist.users;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.seuunng.todolist.lists.ListsEntity;
import com.seuunng.todolist.tasks.TasksEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UsersEntity implements UserDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	private String nickname;
    
    @Column(nullable = true)
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
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;
    
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;
    
    @PrePersist
    protected void onCreate() {
        if (created_at == null) {
            created_at = new Date();
        }
        if (nickname == null || nickname.isEmpty()) {
            nickname = email; 
        }
    }
    public List<String> getRoles() {
        return Collections.singletonList(role.name());
    }
    // 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }

    // 이메일을 사용자 이름으로 반환
    @Override
    public String getUsername() {
        return email;
    }
    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 // 자격 증명 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ListsEntity> lists;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<TasksEntity> tasks;

}
