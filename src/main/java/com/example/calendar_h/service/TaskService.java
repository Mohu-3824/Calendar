package com.example.calendar_h.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
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

    /** 直近タスク取得（上位5件） */
    public List<Task> getRecentTasks(int limit) {
        return taskRepository.findRecentTasks(PageRequest.of(0, limit));
    }

    /** タスク保存 */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /** タスク編集 */
    public Optional<Task> getTaskById(int id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Task task) {
        // 存在確認（見つからなければエラー）
        Task existing = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existing.setTitle(task.getTitle());
        existing.setCategoryCode(task.getCategoryCode());
        existing.setMydate(task.getMydate());
        // ※ 繰り返し設定やその他の項目もあればここに追加
        return taskRepository.save(existing);
    }
    
    /** タスク削除 */
    public void deleteTask(int taskId) {
        taskRepository.deleteById(taskId);
    }
    
    /** タスクの完了状態をトグル */
    public void toggleTaskCompletion(int taskId, boolean isCompleted) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setDone(isCompleted);
        taskRepository.save(task);
    }
}