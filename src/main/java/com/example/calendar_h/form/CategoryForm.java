package com.example.calendar_h.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryForm {
   @NotBlank(message = "カテゴリー名を入力してください。")
   private String categoryName;
   
   private String iconImage;   
   private String colorCode;
}
