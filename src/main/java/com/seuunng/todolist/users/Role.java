package com.seuunng.todolist.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");  // 관리자 역할 추가

    private final String value;
}