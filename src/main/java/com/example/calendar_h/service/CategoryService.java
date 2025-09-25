package com.example.calendar_h.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<Category> getAll() {

		List<Category> categoryList = categoryRepository.findAll();

		return categoryList;

	}

	public List<Category> getByUserId(Integer userId) {
		return categoryRepository.findByUser_Id(userId);
	}
	
	
	// カテゴリーのidおよびユーザーidを取得
	public Category findByIdAndUserId(Integer categoryId, Integer userId) {
	    return categoryRepository.findByIdAndUser_Id(categoryId, userId)
	            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません"));
	}
	
	// カテゴリーの削除
	@Transactional
	public void deleteCategory(Integer categoryId, Integer userId) {
	    Category category = categoryRepository.findByIdAndUser_Id(categoryId, userId)
	            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません"));
	    categoryRepository.delete(category);
	}
}
