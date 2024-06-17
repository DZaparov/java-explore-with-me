package ru.practicum.ewm.category.dto;

import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(
                null,
                newCategoryDto.getName()
        );
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }

    public static Category toCategory(Long categoryId, CategoryDto categoryDto) {
        return new Category(
                categoryId,
                categoryDto.getName()
        );
    }
}
