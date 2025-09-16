package com.example.calendar_h.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.calendar_h.dto.CategoryRequestDto;
import com.example.calendar_h.dto.CategoryResponseDto;
import com.example.calendar_h.entity.Category;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	// カテゴリ作成
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        User user = new User();
        user.setId(requestDto.getUserId());

        Category category = Category.builder()
                .user(user)
                .categoryName(requestDto.getCategoryName())
                .iconImage(requestDto.getIconImage())
                .colorCode(requestDto.getColorCode())
                .build();

        Category saved = categoryRepository.save(category);

        return CategoryResponseDto.builder()
                .id(saved.getId())
                .categoryName(saved.getCategoryName())
                .iconImage(saved.getIconImage())
                .colorCode(saved.getColorCode())
                .build();
    }

    // ユーザー別カテゴリ一覧
    public List<CategoryResponseDto> getCategoriesByUser(Integer userId) {
        User user = new User();
        user.setId(userId);

        return categoryRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(cat -> CategoryResponseDto.builder()
                        .id(cat.getId())
                        .categoryName(cat.getCategoryName())
                        .iconImage(cat.getIconImage())
                        .colorCode(cat.getColorCode())
                        .build())
                .collect(Collectors.toList());
    }
}
