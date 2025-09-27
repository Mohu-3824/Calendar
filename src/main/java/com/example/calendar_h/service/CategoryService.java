package com.example.calendar_h.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	
	// アイコンファイル一覧を取得
    public List<String> getAvailableIconFiles() {
        try {
            File folder = new File("src/main/resources/static/img");
            if (folder.exists() && folder.isDirectory()) {
                return Arrays.stream(folder.listFiles())
                        .filter(f -> !f.isDirectory())
                        .map(File::getName)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // フォルダがない場合のデフォルト
        return List.of("default.png");
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
