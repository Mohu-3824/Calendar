package com.example.calendar_h.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.calendar_h.entity.Task;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.repository.TaskRepository;

@Service
public class TaskService {
	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	// ユーザーIDと日付に基づいてタスクを取得
	public List<Task> getTasksByUserAndDate(Integer userId, LocalDate logDate) {
		return taskRepository.findDistinctByUser_IdAndLogDate(userId, logDate);
	}

	// カレンダー用：その日にタスクが1件でもあるか
	public boolean hasTasksOnDate(Integer userId, LocalDate date) {
		return taskRepository.existsByUser_IdAndLogDate(userId, date);
	}

	// 累計達成日数を取得
	public long getTotalCompletedDays(Integer userId) {
		return taskRepository.countDistinctLogDateByUser_IdAndStatus(userId, true);
	}

	// 連続達成日数を取得
	public long getConsecutiveCompletedDays(Integer userId) {
		// 完了したタスクの日付一覧を取得
		List<Task> completedTasks = taskRepository.findDistinctLogDateByUser_IdAndStatus(userId, true);

		// 日付順に並び替え
		completedTasks.sort(Comparator.comparing(Task::getLogDate));

		long consecutiveDays = 0;
		LocalDate previousDate = null;

		// 連続する日付をカウント
		for (Task task : completedTasks) {
			LocalDate currentDate = task.getLogDate();

			if (previousDate == null || currentDate.isEqual(previousDate.plusDays(1))) {
				consecutiveDays++;
			} else {
				consecutiveDays = 1; // 新しい連続の開始
			}

			previousDate = currentDate;
		}

		return consecutiveDays;
	}

	// タスクIDとユーザーIDに基づいてタスクを取得
	public Optional<Task> getTaskByIdAndUser(Integer taskId, Integer userId) {
		return taskRepository.findByIdAndUser_Id(taskId, userId);
	}

	// タスク削除
	@Transactional
	public void deleteTask(Task task) {
		taskRepository.delete(task);
	}

	// タスク新規作成
	@Transactional
	public Task createTask(User user, String title, LocalDate logDate) {
		Task t = new Task();

		t.setUser(user);
		t.setTitle(title);
		t.setLogDate(logDate);
		t.setStatus(false); // 新規は未完了で作成
		return taskRepository.save(t);
	}

	// タスク更新
	@Transactional
	public void updateTask(Integer taskId, Integer userId, String title, LocalDate logDate) {
		Task task = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new IllegalArgumentException("タスクが見つかりません"));
		task.setTitle(title);
		task.setLogDate(logDate);
		taskRepository.save(task);
	}
}
