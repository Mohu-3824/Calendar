package com.example.calendar_h.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.calendar_h.entity.TaskLog;
import com.example.calendar_h.repository.TaskLogRepository;
import com.example.calendar_h.security.UserDetailsImpl;

@Service
public class TaskLogService {
    private final TaskLogRepository taskLogRepository;

    public TaskLogService(TaskLogRepository taskLogRepository) {
    	this.taskLogRepository = taskLogRepository;
    }
    
    /** 🔑 現在ログイン中のユーザーIDを取得 */
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser().getId();
        } else {
            throw new RuntimeException("ログインユーザー情報が取得できません");
        }
    }
    
    /** 特定日タスクログ取得（現在のログインユーザー） */
    public List<TaskLog> getLogsByDate(LocalDate date) {
        return taskLogRepository.findDistinctByUser_IdAndLogDate(getCurrentUserId(), date);
    }

    /** 直近ログ取得（現在のログインユーザー） */
    public List<TaskLog> getRecentLogs(int limit) {
        return taskLogRepository.findByUser_IdOrderByLogDateDesc(getCurrentUserId(), PageRequest.of(0, limit));
    }

    /** IDでログ取得（Optional） */
    public Optional<TaskLog> getLogById(Integer id) {
        return taskLogRepository.findById(id);
    }
    
    /** 新規作成（現在ログインユーザーに紐付け） */
    public TaskLog createLog(TaskLog log) {
        // 認証ユーザーを紐付け
        log.getUser().setId(getCurrentUserId());
        return taskLogRepository.save(log);
    }

    /** 更新 */
    public TaskLog updateLog(TaskLog log) {
        TaskLog existing = taskLogRepository.findById(log.getId())
                .orElseThrow(() -> new RuntimeException("タスクが見つかりません。"));

        // 更新するフィールドのみ反映
        existing.setLogDate(log.getLogDate());
        existing.setDone(log.isDone());
        existing.setCurrentStreak(log.getCurrentStreak());
        existing.setMaxStreak(log.getMaxStreak());

        return taskLogRepository.save(existing);
    }

    /** 完了状態のトグル */
    public void toggleCompletion(Integer id, boolean isCompleted) {
        TaskLog log = taskLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("タスクが見つかりません。"));
        log.setDone(isCompleted);
        taskLogRepository.save(log);
    }

    /** 削除 */
    public void deleteLog(Integer id) {
        taskLogRepository.deleteById(id);
    }
}