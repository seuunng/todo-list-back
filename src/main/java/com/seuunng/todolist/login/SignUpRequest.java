package com.seuunng.todolist.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequest {
	 private String email;
	    private String password;
	    private String nickname;
}
