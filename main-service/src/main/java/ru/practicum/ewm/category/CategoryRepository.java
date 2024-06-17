package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIdIn(List<Long> categories);

    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}
