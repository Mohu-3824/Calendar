package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

/** 新規タスクフォーム表示 */
@GetMapping("/new")
public String newTaskForm(@RequestParam(required = false) String date, Model model) {
    LocalDate selectedDate = (date == null || date.isBlank())
            ? LocalDate.now()
            : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));


    model.addAttribute("selectedDate", selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 表示用
    model.addAttribute("recentTasks", taskService.getRecentTasks(5));
    model.addAttribute("task", new Task());
    model.addAttribute("editMode", false);
    model.addAttribute("weekdays", List.of("Sun","Mon","Tue","Wed","Thu","Fri","Sat"));

    return "daytask/newtask"; // newtask.html
}

/** 新規タスク保存 */
@PostMapping("/new")
public String createTask(@ModelAttribute Task task,
                         @RequestParam String mydate) {
    task.setMydate(LocalDate.parse(mydate, DF));
    // 初期ステータス（達成状態やストリーク数）
    task.setDone(false);
    task.setCurrentStreak(0);
    task.setMaxStreak(0);

    taskService.createTask(task);

    return "redirect:/tasks/" + mydate;
}

/** 編集フォーム表示 */
@GetMapping("/edit/{taskId}")
public String editTaskForm(@PathVariable int taskId,
                           @RequestParam(required = false) String date,
                           Model model) {
    LocalDate selectedDate = (date == null || date.isBlank())
            ? LocalDate.now()
            : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    Task existingTask = taskService.getTaskById(taskId)
                        .orElseThrow(() -> new RuntimeException("タスクが見つかりません。"));

    model.addAttribute("selectedDate", selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    model.addAttribute("recentTasks", taskService.getRecentTasks(5));
    model.addAttribute("task", existingTask);
    model.addAttribute("editMode", true);
    model.addAttribute("weekdays", List.of("Sun","Mon","Tue","Wed","Thu","Fri","Sat"));

    return "daytask/newtask"; // 同じ画面を使う
}

/** タスク更新 */
@PostMapping("/update")
public String updateTask(@ModelAttribute Task task,
                         @RequestParam String mydate) {
    task.setMydate(LocalDate.parse(mydate, DF));
    taskService.updateTask(task);
    return "redirect:/tasks/" + mydate;
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
