package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.calendar_h.entity.Category;
import com.example.calendar_h.service.CategoryService;
import com.example.calendar_h.service.TaskLogService;

@Controller
public class CalendarController {
	private final TaskLogService taskLogService;
	private final CategoryService categoryService;

	public CalendarController(TaskLogService taskLogService, CategoryService categoryService) {
		this.taskLogService = taskLogService;
		this.categoryService = categoryService;
	}

	@GetMapping("/calendar")
	public String showCalendar(Model model) {
		// 今日の日付を渡す
		model.addAttribute("today", LocalDate.now().toString()); // "2025-09-10"の形式
		return "calendar/index";
	}

	@GetMapping("/calendar/taskdays")
	@ResponseBody
	public List<String> getTaskDays(@RequestParam(required = false) List<String> dates) {
		if (dates == null || dates.isEmpty()) {
			return List.of(); // nullや空配列なら空返す
		}
		return dates.stream()
				.filter(dateStr -> {
					try {
						LocalDate d = LocalDate.parse(dateStr);
						return !taskLogService.getLogsByDate(d).isEmpty();
					} catch (DateTimeParseException e) {
						return false;
					}
				})
				.toList();
	}

	@GetMapping("/categoryList")
	public String getCategoryList(Model model) {
		List<Category> categoryList = categoryService.getAll();
		model.addAttribute("categoryList", categoryList);
		return "categoryList/index";
	}

}
