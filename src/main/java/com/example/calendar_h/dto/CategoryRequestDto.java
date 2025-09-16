package com.example.calendar_h.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDto {

    @NotNull(message = "ユーザーIDは必須です")
    private Integer userId;

    @NotBlank(message = "カテゴリー名は必須です")
    @Size(max = 20, message = "カテゴリー名は20文字以内で入力してください")
    private String categoryName;

    @Size(max = 255, message = "アイコンパスは255文字以内で入力してください")
    private String iconImage;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "色コードは#から始まる6桁の16進数で入力してください")
    private String colorCode;
}
