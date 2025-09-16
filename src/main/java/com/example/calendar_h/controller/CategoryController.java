package com.example.calendar_h.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.calendar_h.dto.CategoryRequestDto;
import com.example.calendar_h.dto.CategoryResponseDto;
import com.example.calendar_h.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	// カテゴリ作成
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.ok(categoryService.createCategory(requestDto));
    }

    // ユーザー別カテゴリ一覧取得
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryResponseDto>> getUserCategories(@PathVariable Integer userId) {
        return ResponseEntity.ok(categoryService.getCategoriesByUser(userId));
    }
}
