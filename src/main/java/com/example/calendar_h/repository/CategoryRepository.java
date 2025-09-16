package com.example.calendar_h.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.entity.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	// 特定ユーザーのカテゴリー一覧を作成日時降順で取得
    List<Category> findByUserOrderByCreatedAtDesc(User user);
}
