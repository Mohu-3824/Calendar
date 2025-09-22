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

	// タスクごとの累計達成日数を取得
	public long getTotalCompletedDaysForTask(Integer userId, String taskTitle) {
		return taskRepository.countDistinctLogDateByUser_IdAndStatusAndTitle(userId, true, taskTitle);
	}

	// タスクごとの連続達成日数を取得
	public long getConsecutiveCompletedDaysForTask(Integer userId, String taskTitle) {
		// 完了したタスクの日付一覧を取得
		List<Task> completedTasks = taskRepository.findDistinctLogDateByUser_IdAndStatusAndTitle(userId, true,
				taskTitle);

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
	public Task createTask(User user, String title, LocalDate logDate, Boolean status) {
		Task t = new Task();

		t.setUser(user);
		t.setTitle(title);
		t.setLogDate(logDate);
		t.setStatus(status != null ? status : false);
		return taskRepository.save(t);
	}

	// タスク更新
	@Transactional
	public void updateTask(Integer taskId, Integer userId, String title, LocalDate logDate, Boolean status) {
		Task t = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new IllegalArgumentException("タスクが見つかりません"));
		t.setTitle(title);
		t.setLogDate(logDate);
		t.setStatus(status != null ? status : false); // nullなら未完了にしておく
		taskRepository.save(t);
	}

	// タスク完了
	@Transactional
	public void updateTaskStatus(Integer taskId, Integer userId, boolean status) {
		Task t = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new IllegalArgumentException("タスクが見つかりません"));
		t.setStatus(status);
		taskRepository.save(t);
	}
}
