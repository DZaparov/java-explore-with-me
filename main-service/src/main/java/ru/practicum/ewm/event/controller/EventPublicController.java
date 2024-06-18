package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@Validated
@AllArgsConstructor
public class EventPublicController {
    public final EventService eventService;

    @GetMapping
    public List<EventShortDto> listEventsPublicFilter(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<@Positive Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        log.info("Попытка получения событий с возможностью фильтрации. " +
                        "text:{}, categories:{}, paid: {}, rangeStart:{}, rangeEnd:{}, " +
                        "onlyAvailable:{}, sort:{}, from:{}, size:{}, request:{}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        List<EventShortDto> result = eventService.listEventsPublicFilter(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRequestURI(), request.getRemoteAddr());

        log.info("Получен список событий: {}", result);

        return result;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventPublicFull(@PathVariable Long id, HttpServletRequest request) {
        log.info("Попытка получения полной информации об опубликованном событии id={}, request:{}", id, request);

        EventFullDto result = eventService.getEventPublicFull(id, request.getRequestURI(), request.getRemoteAddr());

        log.info("Получено событие: {}", result);

        return result;
    }
}
