package com.example.calendar_h.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);
}
