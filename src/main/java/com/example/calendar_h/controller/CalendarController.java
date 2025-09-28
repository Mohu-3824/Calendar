package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.calendar_h.entity.Task;
import com.example.calendar_h.security.UserDetailsImpl;
import com.example.calendar_h.service.TaskService;

@Controller
public class CalendarController {

	private final TaskService taskService;

	public CalendarController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/calendar")
	public String showCalendar(
			@AuthenticationPrincipal UserDetailsImpl principal,
			Model model) {
		LocalDate today = LocalDate.now();
		Integer userId = principal.getUser().getId();

		// 表示月の1日〜末日までの日付一覧を準備
		LocalDate firstDay = today.withDayOfMonth(1);
		LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

		// 表示範囲の日付をリスト化
		List<LocalDate> dateList = firstDay.datesUntil(lastDay.plusDays(1))
				.toList();

		// サービスを使って達成済みタスク名、カテゴリーカラーマップ取得
		Map<LocalDate, List<Map<String, String>>> completedMap =
			    taskService.getCompletedTasksWithColors(userId, dateList);

		// 今日の日付とタスクデータをModelにセット
		model.addAttribute("today", today.toString()); // "2025-09-10"の形式
		// LocalDateキーを文字列に変換して渡す
		model.addAttribute("completedTasks", completedMap.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)));
		
	    // ランキングデータを渡す
	    List<Task> totalRank = taskService.getTotalCompletedDaysRanking(userId, 5);
	    List<Task> consecutiveRank = taskService.getConsecutiveCompletedDaysRanking(userId, 5);

	    model.addAttribute("totalRank", totalRank);
	    model.addAttribute("consecutiveRank", consecutiveRank);

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

	@GetMapping("/calendar/completed-tasktitles")
	@ResponseBody
	public Map<String, List<Map<String, String>>> getCompletedTaskDetails(
			@AuthenticationPrincipal UserDetailsImpl principal,
			@RequestParam List<String> dates) {

		Integer userId = principal.getUser().getId();
		List<LocalDate> localDates = dates.stream()
				.map(LocalDate::parse)
				.toList();

		Map<LocalDate, List<Map<String, String>>> detailsMap =
		        taskService.getCompletedTasksWithColors(userId, localDates);

		// LocalDateキーをStringに変換
	    return detailsMap.entrySet().stream()
	        .collect(Collectors.toMap(
	            e -> e.getKey().toString(),
	            Map.Entry::getValue
	        ));
	}
}
