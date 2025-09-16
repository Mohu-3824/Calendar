package com.example.calendar_h.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "task_master")
public class TaskMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "title")
    private String title;

    @Column(name = "category_code")
    private String categoryCode; 

    @Column(name = "repeat_type")
    private String repeatType; 

    @Column(name = "repeat_frequency")
    private String repeatFrequency; 

    @Column(name = "repeat_weekdays")
    private String repeatWeekdays; 

    @Column(name = "repeat_month_day")
    private Integer repeatMonthDay; 

    @Column(name = "repeat_end_date")
    private LocalDate repeatEndDate; 
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskLog> logs;
}
