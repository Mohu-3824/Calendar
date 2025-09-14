package com.example.calendar_h.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.calendar_h.entity.TaskMaster;

public interface TaskMasterRepository extends JpaRepository<TaskMaster, Integer> {
	 List<TaskMaster> findByUserId(Integer userId);
}
