package com.example.calendar_h.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.form.CategoryForm;
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
	
	// カテゴリー新規登録
	@Transactional
	public void saveCategory(CategoryForm form, Integer userId) {
	    Category category = new Category();
	    User user = new User();
	    user.setId(userId);
	    category.setUser(user);

	    category.setCategoryName(form.getCategoryName());
	    category.setIconImage(form.getIconImage());
	    category.setColorCode(form.getColorCode());

	    categoryRepository.save(category);
	}

	// カテゴリー更新
	@Transactional
	public void updateCategory(CategoryForm form, Integer categoryId, Integer userId) {
	    Category category = categoryRepository.findByIdAndUser_Id(categoryId, userId)
	            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません"));

	    category.setCategoryName(form.getCategoryName());
	    category.setIconImage(form.getIconImage());
	    category.setColorCode(form.getColorCode());

	    categoryRepository.save(category);
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
