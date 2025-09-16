package com.example.calendar_h.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDto {
    private Integer id;
    private String categoryName;
    private String iconImage;
    private String colorCode;
}