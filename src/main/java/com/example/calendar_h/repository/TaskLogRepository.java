package com.example.calendar_h.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.calendar_h.entity.TaskLog;

public interface TaskLogRepository extends JpaRepository<TaskLog, Integer> {

    /**
     * 特定ユーザー & 特定日のタスクログを取得（重複なし）
     */
    @Query("SELECT DISTINCT l FROM TaskLog l " +
           "JOIN FETCH l.task t " +
           "WHERE l.user.id = :userId AND l.logDate = :logDate")
    List<TaskLog> findDistinctByUser_IdAndLogDate(
        @Param("userId") Integer userId,
        @Param("logDate") LocalDate logDate
    );
    
    /**
     * 特定ユーザーの直近タスクログ取得
     * → Pageableで件数制限可能
     */
    List<TaskLog> findByUser_IdOrderByLogDateDesc(Integer userId, Pageable pageable);

    /**
     * 完了フラグでのフィルタ
     */
    List<TaskLog> findByUser_IdAndDoneOrderByLogDateDesc(Integer userId, boolean done);

    /**
     * ユーザーごとの最大連続達成記録
     */
    @Query("SELECT MAX(l.maxStreak) FROM TaskLog l WHERE l.user.id = :userId")
    Integer findMaxStreakByUser(@Param("userId") Integer userId);
}