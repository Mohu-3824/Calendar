package com.example.calendar_h.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.form.CategoryForm;
import com.example.calendar_h.security.UserDetailsImpl;
import com.example.calendar_h.service.CategoryService;

import jakarta.validation.Valid;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categoryList")
    @PreAuthorize("isAuthenticated()")
    public String showCategories(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        if (principal == null || principal.getUser() == null) {
            // 未ログイン時はログインページへ
            return "redirect:/login";
        }

        Integer userId = principal.getUser().getId();
        List<Category> categories = categoryService.getByUserId(userId);

        model.addAttribute("categoryList", categories);
        return "categoryList/index"; // => categoryList.html
    }
    
    // 共通で渡す処理をメソッド化
    private void addIconAndColorList(Model model) {
        model.addAttribute("iconList", List.of("exercise.png", "undo.png", "hobby.jpg"));
        model.addAttribute("colorList", List.of("#ff0000","#ffa500","#ffff00","#008000","#0000ff","#800080"));
    }
    
    // カテゴリー新規作成
    @GetMapping("/categories/new")
    @PreAuthorize("isAuthenticated()")
    public String newCategory(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        if (principal == null || principal.getUser() == null) {
            return "redirect:/login";
        }

        CategoryForm form = new CategoryForm();
        model.addAttribute("categoryForm", form);
        model.addAttribute("isEdit", false);

        addIconAndColorList(model);
        
        return "categoryList/edit";
    }
    
    // カテゴリー新規登録
    @PostMapping("/categories/save")
    @PreAuthorize("isAuthenticated()")
    public String saveCategory(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetailsImpl principal,
                               Model model) {

        if (principal == null || principal.getUser() == null) {
            return "redirect:/login";
        }
        Integer userId = principal.getUser().getId();

        if (bindingResult.hasErrors()) {
            // アイコンリスト・カラーリストを再設定してフォーム再表示
            addIconAndColorList(model);
            model.addAttribute("isEdit", false);
            return "categoryList/edit";
        }

        categoryService.saveCategory(form, userId);
        return "redirect:/categoryList";
    }
    
    // カテゴリー編集
    @GetMapping("/categories/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editCategory(@PathVariable("id") Integer categoryId,
                               @AuthenticationPrincipal UserDetailsImpl principal,
                               Model model) {
        if (principal == null || principal.getUser() == null) {
            return "redirect:/login";
        }
        Integer userId = principal.getUser().getId();

        Category category = categoryService.findByIdAndUserId(categoryId, userId);
        if (category == null) {
            // 存在しなければ一覧にリダイレクト＋エラーメッセージ
            return "redirect:/categoryList";
        }

        // 編集用フォームオブジェクト作成
        CategoryForm form = new CategoryForm();
        form.setCategoryName(category.getCategoryName());
        form.setIconImage(category.getIconImage());
        form.setColorCode(category.getColorCode());

        model.addAttribute("categoryForm", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("categoryId", categoryId);

        addIconAndColorList(model);
        
        return "categoryList/edit"; // category/edit.html に遷移
    }
    
    // カテゴリー更新
    @PostMapping("/categories/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateCategory(@PathVariable("id") Integer categoryId,
                                 @Valid @ModelAttribute("categoryForm") CategoryForm form,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetailsImpl principal,
                                 Model model) {
        if (principal == null || principal.getUser() == null) {
            return "redirect:/login";
        }
        Integer userId = principal.getUser().getId();

        if (bindingResult.hasErrors()) {
            addIconAndColorList(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", categoryId);
            return "categoryList/edit";
        }

        categoryService.updateCategory(form, categoryId, userId);
        return "redirect:/categoryList";
    }
    
    // カテゴリー削除
    @PostMapping("/categories/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteCategory(
            @PathVariable("id") Integer categoryId,
            @AuthenticationPrincipal UserDetailsImpl principal,
            RedirectAttributes ra) {

        if (principal == null || principal.getUser() == null) {
            return "redirect:/login";
        }

        Integer userId = principal.getUser().getId();

        try {
            categoryService.deleteCategory(categoryId, userId);
            // ✅ 削除成功メッセージを次の画面に渡す
            ra.addFlashAttribute("successMessage", "カテゴリーを削除しました。");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categoryList"; // 一覧にリダイレクト
    }  
    
    
}
