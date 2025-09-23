package com.example.calendar_h.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findAll();

	List<Category> findByUser_Id(Integer userId);

	Optional<Category> findByIdAndUser_Id(Integer categoryId, Integer userId);

}
