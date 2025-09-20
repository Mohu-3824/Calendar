package com.example.calendar_h.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
	// 特定ユーザー・特定日付のタスク一覧を取得
	List<Task> findDistinctByUser_IdAndLogDate(Integer userId, LocalDate logDate);

	// 存在チェック用（高速 & 件数不要）
	boolean existsByUser_IdAndLogDate(Integer userId, LocalDate logDate);

	// 累計達成日数を取得するため、完了したタスクの日付のカウント
	long countDistinctLogDateByUser_IdAndStatus(Integer userId, boolean status);

	// 連続達成日数を取得するため、完了したタスクの日付一覧を取得
	List<Task> findDistinctLogDateByUser_IdAndStatus(Integer userId, boolean status);
}
