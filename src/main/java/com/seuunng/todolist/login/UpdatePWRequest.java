package com.seuunng.todolist.login;

import lombok.Getter;
import lombok.Setter;

public class UpdatePWRequest {

    private String email;
    private String password;
    private String newPassword;

    // 기본 생성자
    public UpdatePWRequest() {}

    // 모든 필드를 받는 생성자
    public UpdatePWRequest(String email, String password, String newPassword) {
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
    }

    // 게터와 세터
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
