package com.example.calendar_h.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.calendar_h.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
	 List<Task> findByMydate(LocalDate mydate);
	 @Query("SELECT t FROM Task t ORDER BY t.mydate DESC")
	 List<Task> findRecentTasks(org.springframework.data.domain.Pageable pageable);
}
