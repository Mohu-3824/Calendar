package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.calendar_h.entity.Task;
import com.example.calendar_h.repository.UserRepository;
import com.example.calendar_h.security.UserDetailsImpl;
import com.example.calendar_h.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public TaskController(UserRepository userRepository, TaskService taskService) {
		this.taskService = taskService;
	}

	// 日ごとのタスク一覧を表示
	@GetMapping({ "/{date}", "", "/" })
	@PreAuthorize("isAuthenticated()")
	public String getTasksByDate(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PathVariable("date") String dateStr, Model model) {

		// 認証チェック
		if (userDetailsImpl == null || userDetailsImpl.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です。");
		}

		// PathVariable を LocalDate に変換
		LocalDate logDate;
		try {
			logDate = LocalDate.parse(dateStr); // yyyy-MM-dd 前提
		} catch (DateTimeParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "日付の形式が不正です (yyyy-MM-dd)。");
		}

		// ログインユーザーのID取得
		Integer userId = userDetailsImpl.getUser().getId();

		// タスク一覧を取得
		List<Task> tasks = Optional.ofNullable(taskService.getTasksByUserAndDate(userId, logDate))
				.orElse(Collections.emptyList());
		// 完了タスクと未完了タスクを分ける
		List<Task> completeTasks = tasks.stream().filter(task -> task.getStatus()).collect(Collectors.toList());
		List<Task> incompleteTasks = tasks.stream().filter(task -> !task.getStatus()).collect(Collectors.toList());

		// 累計達成日数と連続達成日数を取得
		long totalCompletedDays = taskService.getTotalCompletedDays(userId);
		long consecutiveCompletedDays = taskService.getConsecutiveCompletedDays(userId);

		// モデルにデータを追加
		model.addAttribute("tasks", tasks);
		model.addAttribute("completeTasks", completeTasks);
		model.addAttribute("incompleteTasks", incompleteTasks);
		model.addAttribute("totalCompletedDays", totalCompletedDays);
		model.addAttribute("consecutiveCompletedDays", consecutiveCompletedDays);
		model.addAttribute("date", logDate.format(DF)); // 表示用文字列
		model.addAttribute("dateObj", logDate); // LocalDate型

		return "daytask/index"; // daytask/index.htmlにデータを渡して表示
	}

	// タスク削除
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	public ResponseEntity<Map<String, String>> deleteTask(@PathVariable("id") Integer taskId,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
		Integer userId = userDetailsImpl.getUser().getId();
		Optional<Task> optionalTask = taskService.getTaskByIdAndUser(taskId, userId);

		if (optionalTask.isPresent()) {
			taskService.deleteTask(optionalTask.get());
			// 成功時は 200 OK + JSON メッセージを返す
			return ResponseEntity.ok(Map.of("message", "タスクを削除しました。"));
		} else {
			// 見つからなければ 404 Not Found を返す
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "タスクが見つかりませんでした。"));
		}
	}
}
