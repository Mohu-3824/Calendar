package com.example.calendar_h.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.calendar_h.entity.TaskMaster;
import com.example.calendar_h.repository.TaskMasterRepository;
import com.example.calendar_h.security.UserDetailsImpl;

@Service
public class TaskMasterService {
    private final TaskMasterRepository taskMasterRepository;

    public TaskMasterService(TaskMasterRepository taskMasterRepository) {
    	this.taskMasterRepository = taskMasterRepository;
    }
    
    /** 🔑 現在ログイン中のユーザーIDを取得 */
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userDetails.getUser().getId();
    }

    /** ログインユーザーの全タスク一覧取得 */
    public List<TaskMaster> getTasksByUser() {
        return taskMasterRepository.findByUserId(getCurrentUserId());
    }
    
    /** IDで取得 */
    public Optional<TaskMaster> getTaskById(Integer id) {
        return taskMasterRepository.findById(id);
    }

    /** 新規作成（現在ログインユーザーに紐付け） */
    public TaskMaster createTask(TaskMaster master) {
        master.getUser().setId(getCurrentUserId());
        return taskMasterRepository.save(master);
    }

    /** 更新 */
    public TaskMaster updateTask(TaskMaster master) {
        TaskMaster existing = taskMasterRepository.findById(master.getId())
                .orElseThrow(() -> new RuntimeException("タスクが見つかりません。"));
        existing.setTitle(master.getTitle());
        existing.setCategoryCode(master.getCategoryCode());
        existing.setRepeatType(master.getRepeatType());
        existing.setRepeatFrequency(master.getRepeatFrequency());
        existing.setRepeatWeekdays(master.getRepeatWeekdays());
        existing.setRepeatMonthDay(master.getRepeatMonthDay());
        existing.setRepeatEndDate(master.getRepeatEndDate());
        return taskMasterRepository.save(existing);
    }

    /** 削除 */
    public void deleteTask(Integer id) {
        taskMasterRepository.deleteById(id);
    }
}
