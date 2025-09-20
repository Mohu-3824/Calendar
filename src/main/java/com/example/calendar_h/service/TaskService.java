package com.example.calendar_h.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.calendar_h.entity.Task;
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
}
