package com.example.calendar_h.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
}
