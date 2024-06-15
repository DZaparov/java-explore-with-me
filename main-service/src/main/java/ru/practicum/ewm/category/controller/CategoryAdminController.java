package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
public class CategoryAdminController {
    public final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED) //201
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Попытка создания категории {}", newCategoryDto);
        CategoryDto result = categoryService.createCategory(newCategoryDto);
        log.info("Создана категория: {}", result);

        return result;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //204
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Попытка удалить категорию с id={}", catId);
        categoryService.deleteCategory(catId);
        log.info("Удалена категория с id={}", catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK) //200
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Попытка обновить категорию с id={} на {}", catId, categoryDto);
        CategoryDto result = categoryService.updateCategory(catId, categoryDto);
        log.info("Обновлена категория: {}", result);

        return result;
    }
}
