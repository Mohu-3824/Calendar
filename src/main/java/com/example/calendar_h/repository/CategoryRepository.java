package com.example.calendar_h.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	
	 List<Category> findAll();

}
