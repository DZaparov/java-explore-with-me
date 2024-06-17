package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/categories")
@Slf4j
public class CategoryPublicController {
    public final CategoryService categoryService;

    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> listCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка получения всех категорий from={}, size={}", from, size);
        List<CategoryDto> result = categoryService.listCategories(from, size);
        log.info("Получен список всех категорий: {}", result);

        return result;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Попытка получения категории id=: {}", catId);
        CategoryDto result = categoryService.getCategoryById(catId);
        log.info("Категория получена: {}", result);

        return result;
    }
}
