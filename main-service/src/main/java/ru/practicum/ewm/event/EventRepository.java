package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findAllByInitiatorId(Long userId, PageRequest page);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    HashSet<Event> findAllByIdIn(Set<Long> events);

    boolean existsByCategoryId(Long categoryId);
}
