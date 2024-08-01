package com.seuunng.todolist.login;

import com.seuunng.todolist.users.UsersEntity;

public class AuthResponse {
	private UsersEntity user;
    private String token;

    // 기본 생성자
    public AuthResponse() {}

    // UsersEntity와 token을 받는 생성자
    public AuthResponse(UsersEntity user, String token) {
        this.user = user;
        this.token = token;
    }
    // UsersEntity만 받는 생성자
    public AuthResponse(UsersEntity user) {
        this.user = user;
    }
    public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}