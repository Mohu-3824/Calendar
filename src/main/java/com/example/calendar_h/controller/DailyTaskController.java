package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.calendar_h.entity.Task;
import com.example.calendar_h.service.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class DailyTaskController {

private final TaskService taskService;
private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

/** 日次タスク表示 */
@GetMapping("/day")
public String task(@RequestParam(required = false) String date, Model model) {
    LocalDate targetDate;
    if (date == null || date.isBlank()) {
        targetDate = LocalDate.now();
    } else {
        targetDate = LocalDate.parse(date, DF);
    }
    List<Task> tasks = taskService.getTasksByDate(targetDate);
    model.addAttribute("tasks", tasks);
    model.addAttribute("date", targetDate.format(DF));
    return "daytask/index";
}

/** 完了トグル */
@PostMapping("/toggle")
public String toggleTaskCompletion(@RequestParam int taskId, @RequestParam boolean done,@RequestParam String date) {
    taskService.toggleTaskCompletion(taskId, done);
    return "redirect:/tasks/day?date=" + date; // 今日の日付でリダイレクト
}

/** タスク削除 */
@PostMapping("/delete")
public String deleteTask(@RequestParam int taskId,@RequestParam String date) {
    taskService.deleteTask(taskId);
    return "redirect:/tasks/day?date=" + date; // 今日の日付でリダイレクト
}
}
