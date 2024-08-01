package com.seuunng.todolist.login;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.seuunng.todolist.users.UsersEntity;

public class AccountContext extends User {
	
    private final UsersEntity account;


public AccountContext(UsersEntity account, Collection<? extends GrantedAuthority> authorities) {
	 super(account.getEmail(), account.getPassword(), authorities);
     this.account = account;
}

public UsersEntity getAccount() {
    return account;
}
}