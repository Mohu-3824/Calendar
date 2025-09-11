package com.example.calendar_h.service;

import java.time.LocalDate;
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
	
    /** 特定の日付のタスクを取得 */
    public List<Task> getTasksByDate(LocalDate date) {
        return taskRepository.findByMydate(date);
    }

    /** タスクの完了状態をトグル */
    public void toggleTaskCompletion(int taskId, boolean isCompleted) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setDone(isCompleted);
        taskRepository.save(task);
    }

    /** 新規タスクを保存 */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /** タスクの削除 */
    public void deleteTask(int taskId) {
        taskRepository.deleteById(taskId);
    }
}