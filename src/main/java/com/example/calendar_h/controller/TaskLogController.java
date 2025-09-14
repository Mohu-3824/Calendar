package com.example.calendar_h.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.calendar_h.entity.TaskLog;
import com.example.calendar_h.entity.TaskMaster;
import com.example.calendar_h.entity.User;
import com.example.calendar_h.repository.UserRepository;
import com.example.calendar_h.service.TaskLogService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/logs")
public class TaskLogController {

    private final TaskLogService taskLogService;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 日次タスク表示 */
    @GetMapping({"/{date}", "", "/"})
    public String showLogs(@PathVariable(required = false) String date, Model model) {
        LocalDate targetDate;

        if (date == null || date.isBlank()) {
            targetDate = LocalDate.now();
        } else {
            String cleanDate = date.replace("\"", "").trim(); // 不要なダブルクオート除去
            try {
                targetDate = LocalDate.parse(cleanDate, DF);
            } catch (DateTimeParseException e) {
                targetDate = LocalDate.now();
            }
        }

        model.addAttribute("date", targetDate.format(DF)); // 表示用文字列
        model.addAttribute("dateObj", targetDate); // LocalDate型

        List<TaskLog> logs = taskLogService.getLogsByDate(targetDate);
        model.addAttribute("incompleteLogs", logs.stream().filter(l -> !l.isDone()).toList());
        model.addAttribute("completedLogs", logs.stream().filter(TaskLog::isDone).toList());

        return "daytask/index";
    }

    /** 新規タスクフォーム表示 */
    @GetMapping("/new")
    public String newLogForm(@RequestParam(required = false) String date, Model model) {
        LocalDate selectedDate = (date == null || date.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(date, DF);

        // TaskLogとTaskMasterを初期化
        TaskLog log = new TaskLog();
        log.setTask(new TaskMaster());
        
        model.addAttribute("selectedDate", selectedDate.format(DF));
        model.addAttribute("recentLogs", taskLogService.getRecentLogs(5));
        model.addAttribute("log", log);
        model.addAttribute("editMode", false);
        model.addAttribute("weekdays", List.of("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));

        return "daytask/newtask";
    }

    /** 新規タスク保存 */
    @PostMapping("/new")
    public String createLog(@ModelAttribute TaskLog log,
                            @RequestParam String logDate, UserRepository userRepository) {
        Integer userId = 1;
        User user = userRepository.findById(userId)
        	    .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.setUser(user);
        log.setLogDate(LocalDate.parse(logDate, DF));
        log.setDone(false);
        log.setCurrentStreak(0);
        log.setMaxStreak(0);

        taskLogService.createLog(log);

        return "redirect:/logs/" + logDate;
    }

    /** 編集フォーム表示 */
    @GetMapping("/edit/{logId}")
    public String editLogForm(@PathVariable Integer logId,
                              @RequestParam(required = false) String date,
                              Model model) {
        LocalDate selectedDate = (date == null || date.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(date, DF);

        TaskLog existingLog = taskLogService.getLogById(logId)
                .orElseThrow(() -> new RuntimeException("タスクログが見つかりません。"));

        model.addAttribute("selectedDate", selectedDate.format(DF));
        model.addAttribute("recentLogs", taskLogService.getRecentLogs(5));
        model.addAttribute("log", existingLog);
        model.addAttribute("editMode", true);
        model.addAttribute("weekdays", List.of("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));

        return "daytask/newtask";
    }

    /** タスク更新 */
    @PostMapping("/update")
    public String updateLog(@ModelAttribute TaskLog log,
                            @RequestParam String logDate) {
        log.setLogDate(LocalDate.parse(logDate, DF));
        taskLogService.updateLog(log);
        return "redirect:/logs/" + logDate;
    }

    /** 完了トグル */
    @PostMapping("/toggle")
    @ResponseBody
    public String toggleCompletion(@RequestParam Integer logId,
                                    @RequestParam boolean done) {
        taskLogService.toggleCompletion(logId, done);
        return "OK";
    }

    /** タスク削除 */
    @PostMapping("/delete")
    public String deleteLog(@RequestParam Integer logId,
                            @RequestParam String date) {
        taskLogService.deleteLog(logId);
        return "redirect:/logs/" + date;
    }
}
