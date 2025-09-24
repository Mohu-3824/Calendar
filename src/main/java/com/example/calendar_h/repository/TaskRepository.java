package com.example.calendar_h.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.calendar_h.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
	// 特定ユーザー・特定日付のタスク一覧を取得
	// カテゴリーも一緒に取得（N+1対策用、省略可能）
	@EntityGraph(attributePaths = {"category"})
	List<Task> findDistinctByUser_IdAndLogDate(Integer userId, LocalDate logDate);

	// 存在チェック用（高速 & 件数不要）
	boolean existsByUser_IdAndLogDate(Integer userId, LocalDate logDate);

	// 累計達成日数を取得するため、完了したタスクの日付のカウント
	long countDistinctLogDateByUser_IdAndStatus(Integer userId, boolean status);

	// 連続達成日数を取得するため、完了したタスクの日付一覧を取得
	List<Task> findDistinctLogDateByUser_IdAndStatus(Integer userId, boolean status);

	// ユーザーIDとタスクIDに基づいてタスクを取得
	Optional<Task> findByIdAndUser_Id(Integer id, Integer userId);

	// 累計達成日数を取得するため、完了したタスクの日付のカウント
	long countDistinctLogDateByUser_IdAndStatusAndTitle(Integer userId, boolean status, String title);

	// 連続達成日数を取得するため、完了したタスクの日付一覧を取得
	List<Task> findDistinctLogDateByUser_IdAndStatusAndTitle(Integer userId, boolean status, String title);

	// ユーザー・日付リストで達成済みタスクタイトル一覧を取得
	@Query("SELECT DISTINCT t.logDate, t.title "
			+ "FROM Task t "
			+ "WHERE t.user.id = :userId "
			+ "AND t.logDate IN :dates "
			+ "AND t.status = true")
	List<Object[]> findCompletedTaskTitlesByUserAndDates(
			@Param("userId") Integer userId,
			@Param("dates") List<LocalDate> dates);

	// ユーザーIDに基づいて直近のタスクを3件取得
	List<Task> findTop50ByUser_IdOrderByLogDateDesc(Integer userId);
}
