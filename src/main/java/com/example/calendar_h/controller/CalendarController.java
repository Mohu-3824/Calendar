package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.calendar_h.entity.Task;
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
	
	@GetMapping("/calendar/day")
    public String showTasksByDate(@RequestParam String date, Model model) {
        LocalDate targetDate = LocalDate.parse(date);
        List<Task> tasks = taskService.getTasksByDate(targetDate);
        model.addAttribute("tasks", tasks);
        model.addAttribute("date", targetDate);
        return "daytask/index";
    }
}
