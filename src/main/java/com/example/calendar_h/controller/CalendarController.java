package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.calendar_h.security.UserDetailsImpl;
import com.example.calendar_h.service.TaskService;

@Controller
public class CalendarController {

	private final TaskService taskService;

	public CalendarController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/calendar")
	public String showCalendar(Model model) {
		// 今日の日付を渡す
		model.addAttribute("today", LocalDate.now().toString()); // "2025-09-10"の形式
		return "calendar/index";
	}

	// タスクがある日をチェックする
	@GetMapping("/calendar/taskdays")
	@ResponseBody
	public List<String> getTaskDays(
			@AuthenticationPrincipal UserDetailsImpl principal,
			@RequestParam(required = false) List<String> dates) {

		if (dates == null || dates.isEmpty())
			return List.of();

		// ユーザーID取得
		final Integer userId = principal.getUser().getId();

		return dates.stream()
				.filter(dateStr -> {
					try {
						LocalDate d = LocalDate.parse(dateStr);
						return taskService.hasTasksOnDate(userId, d);
					} catch (DateTimeParseException e) {
						return false;
					}
				})
				.toList();
	}
}
