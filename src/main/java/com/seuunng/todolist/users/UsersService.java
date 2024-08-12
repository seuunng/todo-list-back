package com.seuunng.todolist.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seuunng.todolist.login.UserDTO;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;
    
    @Transactional(readOnly = true)
    public UserDTO getUserWithLists(String email) {
        UsersEntity user = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setNickname(user.getNickname());
        // 필요한 다른 필드들을 설정
        return userDTO;
    }
}
