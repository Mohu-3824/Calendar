package com.example.calendar_h.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.calendar_h.entity.TaskMaster;
import com.example.calendar_h.service.TaskMasterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/master")
public class TaskMasterController {

    private final TaskMasterService taskMasterService;

    /** タスク一覧表示（ログインユーザーの） */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskMasterService.getTasksByUser());
        return "master/index";
    }

    /** 新規作成フォーム */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("taskMaster", new TaskMaster());
        model.addAttribute("frequencies", List.of("daily", "weekly", "monthly"));
        model.addAttribute("weekdays", List.of("Sun","Mon","Tue","Wed","Thu","Fri","Sat"));
        return "master/form";
    }

    /** 保存 */
    @PostMapping("/new")
    public String create(@ModelAttribute TaskMaster taskMaster) {
        taskMasterService.createTask(taskMaster);
        return "redirect:/master";
    }

    /** 編集フォーム */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        TaskMaster task = taskMasterService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("タスクが見つかりません"));
        model.addAttribute("taskMaster", task);
        model.addAttribute("frequencies", List.of("daily", "weekly", "monthly"));
        model.addAttribute("weekdays", List.of("Sun","Mon","Tue","Wed","Thu","Fri","Sat"));
        return "master/form";
    }

    /** 更新 */
    @PostMapping("/update")
    public String update(@ModelAttribute TaskMaster taskMaster) {
        taskMasterService.updateTask(taskMaster);
        return "redirect:/master";
    }

    /** 削除 */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        taskMasterService.deleteTask(id);
        return "redirect:/master";
    }
}
