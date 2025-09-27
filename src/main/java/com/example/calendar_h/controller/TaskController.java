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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.entity.Task;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.form.TaskRegisterForm;
import com.example.calendar_h.repository.UserRepository;
import com.example.calendar_h.security.UserDetailsImpl;
import com.example.calendar_h.service.CategoryService;
import com.example.calendar_h.service.TaskService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;
	private final CategoryService categoryService;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public TaskController(UserRepository userRepository, TaskService taskService, CategoryService categoryService) {
		this.taskService = taskService;
		this.categoryService = categoryService;
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

		// 各タスクの累計達成日数と連続達成日数を取得
		for (Task task : tasks) {
			long totalCompletedDaysForTask = taskService.getTotalCompletedDaysForTask(userId, task.getTitle());
			long consecutiveCompletedDaysForTask = taskService.getConsecutiveCompletedDaysForTask(userId,
					task.getTitle());

			task.setTotalCompletedDays(totalCompletedDaysForTask);
			task.setConsecutiveCompletedDays(consecutiveCompletedDaysForTask);
		}
		// モデルにデータを追加
		model.addAttribute("tasks", tasks);
		model.addAttribute("completeTasks", completeTasks);
		model.addAttribute("incompleteTasks", incompleteTasks);
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

	// タスク新規作成
	@GetMapping("/new")
	@PreAuthorize("isAuthenticated()")
	public String newTask(
			@AuthenticationPrincipal UserDetailsImpl principal,
			@RequestParam(name = "date", required = false) String dateStr,
			Model model) {

		// ログインチェック（既存方針に合わせる）
		if (principal == null || principal.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です。");
		}

		TaskRegisterForm form = new TaskRegisterForm();

		LocalDate date;
		try {
			date = (dateStr != null && !dateStr.isBlank())
					? LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
					: LocalDate.now();
		} catch (DateTimeParseException e) {
			// フォーマットがおかしい場合は今日の日付にフォールバック
			date = LocalDate.now();
		}

		form.setLogDate(date);

		model.addAttribute("taskForm", form);
		model.addAttribute("isEdit", false);

		// 直近3件取得して渡す
		Integer userId = principal.getUser().getId();
		model.addAttribute("recentTasks", taskService.getRecentTasksByUser(userId));

		// ★カテゴリー一覧
		model.addAttribute("categories", categoryService.getByUserId(userId));

		return "daytask/new";
	}

	// タスク登録処理
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public String createTask(
			@AuthenticationPrincipal UserDetailsImpl principal,
			@Valid @ModelAttribute("taskForm") TaskRegisterForm form,
			BindingResult bindingResult,
			RedirectAttributes ra,
			Model model) {

		if (principal == null || principal.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です。");
		}

		if (bindingResult.hasErrors()) {
			// カテゴリー一覧再取得（エラー時再描画用）
			List<Category> categories = categoryService.getByUserId(principal.getUser().getId());
			model.addAttribute("categories", categories);
			return "daytask/new";
		}

		User user = principal.getUser();

		try {
			taskService.createTask(user, form.getTitle(), form.getLogDate(), form.getStatus(), form.getCategoryId());
			ra.addFlashAttribute("successMessage", "タスクを登録しました。");
		} catch (IllegalArgumentException e) {
			bindingResult.reject("", e.getMessage());
			List<Category> categories = categoryService.getByUserId(principal.getUser().getId());
			model.addAttribute("categories", categories);
			return "daytask/new";
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			// (user_id, log_date, title) の UNIQUE 制約にひっかかった場合など
			bindingResult.reject("", "同じタイトルのタスクが同じ日に既に存在します。");
			return "daytask/new";
		}

		// 登録した日の一覧へ
		return "redirect:/tasks/" + form.getLogDate().format(DF);
	}

	// タスク編集
	@GetMapping("/edit/{id}")
	@PreAuthorize("isAuthenticated()")
	public String editTask(@PathVariable("id") Integer taskId,
			@AuthenticationPrincipal UserDetailsImpl principal,
			Model model) {
		if (principal == null || principal.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です。");
		}
		Integer userId = principal.getUser().getId();

		Task task = taskService.getTaskByIdAndUser(taskId, userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "タスクが見つかりません"));

		TaskRegisterForm form = new TaskRegisterForm();
		form.setTitle(task.getTitle());
		form.setLogDate(task.getLogDate());
		form.setStatus(task.getStatus());
		form.setCategoryId(task.getCategory() != null ? task.getCategory().getId() : null);

		model.addAttribute("taskForm", form);
		model.addAttribute("taskId", taskId); // 更新用に必要
		model.addAttribute("isEdit", true); // 新規か編集かを判定するフラグ
		model.addAttribute("recentTasks", taskService.getRecentTasksByUser(userId));

		// ★カテゴリー一覧
		model.addAttribute("categories", categoryService.getByUserId(userId));

		return "daytask/new"; // 新規作成画面を再利用
	}

	// タスク更新処理
	@PostMapping("/update/{id}")
	@PreAuthorize("isAuthenticated()")
	public String updateTask(@PathVariable("id") Integer taskId,
			@AuthenticationPrincipal UserDetailsImpl principal,
			@Valid @ModelAttribute("taskForm") TaskRegisterForm form,
			BindingResult bindingResult,
			RedirectAttributes ra,
			Model model) {
		if (principal == null || principal.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ログインが必要です。");
		}

		Integer userId = principal.getUser().getId();

		if (bindingResult.hasErrors()) {
			List<Category> categories = categoryService.getByUserId(userId);
			model.addAttribute("categories", categories);
			model.addAttribute("isEdit", true);
			model.addAttribute("taskId", taskId);
			return "daytask/new";
		}

		try {
			// ★安全版 TaskService 呼び出し（categoryId 付き）
			taskService.updateTask(taskId, userId, form.getTitle(), form.getLogDate(), form.getStatus(),
					form.getCategoryId());
			ra.addFlashAttribute("successMessage", "タスクを更新しました。");
		} catch (IllegalArgumentException e) {
			bindingResult.reject("", e.getMessage());
			List<Category> categories = categoryService.getByUserId(userId);
			model.addAttribute("categories", categories);
			model.addAttribute("isEdit", true);
			model.addAttribute("taskId", taskId);
			return "daytask/new";
		}

		return "redirect:/tasks/" + form.getLogDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	// タスク完了
	@PostMapping("/toggle")
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	public ResponseEntity<?> toggleTask(@AuthenticationPrincipal UserDetailsImpl principal,
			@RequestParam("taskId") Integer taskId,
			@RequestParam("done") boolean done,
			@RequestParam("date") String dateStr) {
		if (principal == null || principal.getUser() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Integer userId = principal.getUser().getId();
		
		try {
	        taskService.updateTaskStatus(taskId, userId, done);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("message", e.getMessage()));
	    }

		// 完了更新の結果を返す
		return ResponseEntity.ok(Map.of("message", "ステータスを更新しました"));
	}
}
