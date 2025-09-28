package com.example.calendar_h.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.entity.Task;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.repository.CategoryRepository;
import com.example.calendar_h.repository.TaskRepository;

@Service
public class TaskService {
	private final TaskRepository taskRepository;
	private final CategoryRepository categoryRepository;

	public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
		this.taskRepository = taskRepository;
		this.categoryRepository = categoryRepository;
	}

	/**
	 * カテゴリーの所有者がユーザーと一致するか検証し、エンティティを返す。
	 * @param categoryId カテゴリーID（null可）
	 * @param userId ログインユーザーID
	 */
	private Category validateAndGetCategory(Integer categoryId, Integer userId) {
		if (categoryId == null) {
			return null; // カテゴリー未選択
		}
		return categoryRepository.findByIdAndUser_Id(categoryId, userId)
				.orElseThrow(() -> new IllegalArgumentException("不正なカテゴリーが指定されました"));
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
	public Task createTask(User user, String title, LocalDate logDate, Boolean status, Integer categoryId) {
		Task t = new Task();
		
		// ★未来日の完了禁止
	    if (status && t.getLogDate().isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException("未来日のタスクは完了にできません");
	    }

		t.setUser(user);
		t.setTitle(title);
		t.setLogDate(logDate);
		t.setStatus(status != null ? status : false);

		// 所有チェック付きカテゴリーセット
		Category category = validateAndGetCategory(categoryId, user.getId());
		t.setCategory(category);

		return taskRepository.save(t);
	}

	// タスク更新
	@Transactional
	public void updateTask(Integer taskId, Integer userId, String title, LocalDate logDate, Boolean status,
			Integer categoryId) {
		Task t = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new IllegalArgumentException("タスクが見つかりません"));
		
		// 未来日の完了禁止
	    if (status != null && status && logDate.isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException("未来日のタスクは完了にできません");
	    }
	    
		t.setTitle(title);
		t.setLogDate(logDate);
		t.setStatus(status != null ? status : false); // nullなら未完了にしておく

		// 所有チェック付きカテゴリーセット
		Category category = validateAndGetCategory(categoryId, userId);
		t.setCategory(category);

		taskRepository.save(t);
	}

	// タスク完了
	@Transactional
	public void updateTaskStatus(Integer taskId, Integer userId, boolean status) {
		Task t = taskRepository.findByIdAndUser_Id(taskId, userId)
				.orElseThrow(() -> new IllegalArgumentException("タスクが見つかりません"));
		
		// ★未来日の完了禁止
	    if (status && t.getLogDate().isAfter(LocalDate.now())) {
	        throw new IllegalArgumentException("未来日のタスクは完了にできません");
	    }
	    
		t.setStatus(status);
		taskRepository.save(t);
	}

	// 指定した日付の達成済みタスク名一覧を取得
	public Map<LocalDate, List<Map<String, String>>> 
		getCompletedTasksWithColors(Integer userId, List<LocalDate> dates) {
		List<Object[]> result = taskRepository.findCompletedTaskTitlesWithColorsByUserAndDates(userId, dates);
		Map<LocalDate, List<Map<String, String>>> map = new HashMap<>();
	    for (Object[] row : result) {
	        LocalDate date = (LocalDate) row[0];
	        String title = (String) row[1];
	        String colorCode = (String) row[2];
	        String iconImage = (String) row[3];
	        
	        Map<String, String> taskInfo = new HashMap<>();
	        taskInfo.put("title", title);
	        taskInfo.put("color", colorCode != null ? colorCode : "#ddd");
	        if (iconImage != null && !iconImage.isBlank()) {
	            taskInfo.put("icon", iconImage);
	        }

	        map.computeIfAbsent(date, k -> new ArrayList<>()).add(taskInfo);
		}
		return map;
	}

	// ログイン済みユーザーの直近3件のタスクを表示する
	public List<Task> getRecentTasksByUser(Integer userId) {
		// ユーザーのタスクを日付降順で取得（たくさん取ってから絞る）
		List<Task> allTasks = taskRepository.findTop50ByUser_IdOrderByLogDateDesc(userId);
		// タイトルの重複を排除しながら3件まで取り出す
		List<Task> uniqueTasks = allTasks.stream()
				.collect(Collectors.toMap(
						Task::getTitle, // key: タイトル
						t -> t, // value: タスク本体
						(existing, replacement) -> existing, // 同じタイトルなら最初のものを残す
						LinkedHashMap::new // 順序保持
				))
				.values()
				.stream()
				.limit(3) // 上位3件
				.collect(Collectors.toList());

		// 各タスクに累計・連続日数をセット
		for (Task task : uniqueTasks) {
			long total = getTotalCompletedDaysForTask(userId, task.getTitle());
			long consecutive = getConsecutiveCompletedDaysForTask(userId, task.getTitle());
			task.setTotalCompletedDays(total);
			task.setConsecutiveCompletedDays(consecutive);
			
	        // カラーコードをセット
	        if (task.getCategory() != null && task.getCategory().getColorCode() == null) {
	            task.getCategory().setColorCode("#ccc"); // デフォルト色
	        }			
		}

		return uniqueTasks;
	}
	
    /**
     * 累計達成日数ランキング上位 limit 件を返す
     */
    @Transactional(readOnly = true)
    public List<Task> getTotalCompletedDaysRanking(Integer userId, int limit) {
        // 完了タスク（カテゴリー込み）を取得
        List<Task> allCompleted = taskRepository.findDistinctByUser_IdAndStatus(userId, true);

        // タイトルで重複排除（最新日付のTaskを代表として使う）
        List<Task> unique = allCompleted.stream()
            .collect(Collectors.toMap(
                Task::getTitle,
                t -> t,
                (existing, replacement) -> existing
            ))
            .values()
            .stream()
            .collect(Collectors.toList());

        // 各タスクに累計日数をセット
        for (Task task : unique) {
            long totalDays = taskRepository
                .countDistinctLogDateByUser_IdAndStatusAndTitle(userId, true, task.getTitle());
            task.setTotalCompletedDays(totalDays);
        }

        // 上位 limit 件を返す
        return unique.stream()
            .sorted((a, b) -> Long.compare(b.getTotalCompletedDays(), a.getTotalCompletedDays()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 連続達成日数ランキング上位 limit 件を返す
     */
    @Transactional(readOnly = true)
    public List<Task> getConsecutiveCompletedDaysRanking(Integer userId, int limit) {
        // 完了タスク（カテゴリー込み）を取得
        List<Task> allCompleted = taskRepository.findDistinctByUser_IdAndStatus(userId, true);

        // タイトルで重複排除
        List<Task> unique = allCompleted.stream()
            .collect(Collectors.toMap(
                Task::getTitle,
                t -> t,
                (existing, replacement) -> existing
            ))
            .values()
            .stream()
            .collect(Collectors.toList());

        // 各タスクに連続日数をセット
        for (Task task : unique) {
            long consecutiveDays = calculateConsecutiveDays(userId, task.getTitle());
            task.setConsecutiveCompletedDays(consecutiveDays);
        }

        // 上位 limit 件を返す
        return unique.stream()
            .sorted((a, b) -> Long.compare(b.getConsecutiveCompletedDays(), a.getConsecutiveCompletedDays()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 連続達成日数計算
     */
    private long calculateConsecutiveDays(Integer userId, String taskTitle) {
        List<Task> completedTasks =
            taskRepository.findDistinctLogDateByUser_IdAndStatusAndTitle(userId, true, taskTitle);

        completedTasks.sort((a, b) -> a.getLogDate().compareTo(b.getLogDate()));

        long maxStreak = 0;
        long currentStreak = 0;
        LocalDate prevDate = null;

        for (Task t : completedTasks) {
            if (prevDate == null || t.getLogDate().isEqual(prevDate.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }
            prevDate = t.getLogDate();
            maxStreak = Math.max(maxStreak, currentStreak);
        }

        return maxStreak;
    }
}
