package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);

        if (categoryRepository.existsByName(category.getName())) {
            throw new ConflictException("Name is not unique");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        applyChecks(categoryId);
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Category has events");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        applyChecks(categoryId);

        Optional<Category> existingCategory = categoryRepository.findByName(categoryDto.getName());
        Category category = CategoryMapper.toCategory(categoryDto);

        if (existingCategory.isPresent()) {
            if (!existingCategory.get().getId().equals(categoryId)) {
                if (existingCategory.get().getName().equals(category.getName())) {
                    throw new ConflictException("Name is not unique");
                }
            }
        }


        category.setId(categoryId);

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> listCategories(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<Category> categories = categoryRepository.findAll(page);

        return categories
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));

        return CategoryMapper.toCategoryDto(category);
    }

    private Category applyChecks(Long catId) {
        if (catId != null) {
            return categoryRepository.findById(catId).orElseThrow(() ->
                    new NotFoundException("Category with id=" + catId + " was not found"));
        }
        return null;
    }
}
