package com.seuunng.todolist.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
=======
>>>>>>> origin/server
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long>{
<<<<<<< HEAD
	Optional<UsersEntity> findByEmail(String email);
	boolean existsByEmail(String email);
=======
	Optional<UsersEntity> findById(String id);
>>>>>>> origin/server
}
