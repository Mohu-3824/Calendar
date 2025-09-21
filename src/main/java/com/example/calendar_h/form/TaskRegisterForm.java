package com.example.calendar_h.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRegisterForm {
	@NotBlank(message = "タスク名を入力してください。")
	private String title;

	@NotNull(message = "日付を入力してください")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate logDate;
}
