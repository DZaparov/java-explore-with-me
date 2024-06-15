package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ToString
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
