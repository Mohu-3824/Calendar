package com.example.calendar_h.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data 
@Table(name = "tasks")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "mydate")
	private LocalDate mydate;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "category_code")
	private String categoryCode;  // study / beauty / exercise / ...
	
	@Column(name = "done")
	private boolean done;
	
	@Column(name = "current_streak")
	private int currentStreak;    // 連続達成日数（例：3日）
	
	@Column(name = "max_streak")
	private int maxStreak;        // 累計最長（例：15日）
}
