
package com.example.demo.repository;

import com.example.demo.entity.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
	
	
	User findByEmailAndPassword(String email, String password);
	
	User findByEmail(String email);
	
	User findByResetToken(String resetToken); // If not already added

}

