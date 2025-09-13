package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@GetMapping({"/{date}", "", "/"})
public String task(@PathVariable(required = false) String date, Model model) {
    LocalDate targetDate = (date == null || date.isBlank())
            ? LocalDate.now()
            : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    model.addAttribute("date", targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 表示用
    model.addAttribute("dateObj", targetDate); // LocalDate型

    List<Task> tasks = taskService.getTasksByDate(targetDate);
    model.addAttribute("incompleteTasks", tasks.stream().filter(t -> !t.isDone()).toList());
    model.addAttribute("completedTasks", tasks.stream().filter(Task::isDone).toList());

    return "daytask/index";
}


/** 完了トグル */
@PostMapping("/toggle")
public String toggleTaskCompletion(
        @RequestParam int taskId,
        @RequestParam boolean done,
        @RequestParam String date
) {
    taskService.toggleTaskCompletion(taskId, done);
    return "redirect:/tasks/" + date;
}
/** タスク削除 */
@PostMapping("/delete")
public String deleteTask(
		@RequestParam int taskId,
		@RequestParam String date
) {
    taskService.deleteTask(taskId);
    return "redirect:/tasks/" + date; // 今日の日付でリダイレクト
}
}
