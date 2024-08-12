package com.seuunng.todolist.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long>{
	Optional<UsersEntity> findByEmail(String email);
	boolean existsByEmail(String email);

}
