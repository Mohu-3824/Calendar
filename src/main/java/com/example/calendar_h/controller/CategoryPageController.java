package com.example.calendar_h.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.calendar_h.dto.CategoryResponseDto;
import com.example.calendar_h.service.CategoryService;

@Controller
public class CategoryPageController {

    private final CategoryService categoryService;
    
    public CategoryPageController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

    // ダミーデータ版（API接続前）
    @GetMapping("/categories")
    public String showCategoryList(Model model) {

        // 仮のデータ（後でAPI呼び出しに置き換え）
        List<CategoryResponseDto> dummyCategories = Arrays.asList(
                CategoryResponseDto.builder()
                        .id(1)
                        .categoryName("運動")
                        .iconImage(null)
                        .colorCode("#FF0000")
                        .build(),
                CategoryResponseDto.builder()
                        .id(2)
                        .categoryName("勉強")
                        .iconImage(null)
                        .colorCode("#00FF00")
                        .build()
        );

        model.addAttribute("categories", dummyCategories);
        model.addAttribute("message", "カテゴリー一覧を表示しました");

        return "category/list";
    }
}
