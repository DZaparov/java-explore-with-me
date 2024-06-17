package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.location.service.LocationService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
public class EventAdminController {
    public final EventService eventService;
    public final LocationService locationService;

    public EventAdminController(EventService eventService, LocationService locationService) {
        this.eventService = eventService;
        this.locationService = locationService;
    }

    @GetMapping
    public List<EventFullDto> listEventsAdminFilter(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка получения полной информации о событиях событий, подходящих под условия. Users:{}, " +
                        "stats:{}, categories:{}, rangeStart:{}, rangeEnd:{}, from:{}, size:{}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> result = eventService.listEventsAdminFilter(
                users, states, categories, rangeStart, rangeEnd, from, size);

        log.info("Получен список событий: {}", result);

        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Попытка изменения события добавленного администратором: {}", updateEventAdminRequest);
        EventFullDto result = eventService.updateEventAdmin(eventId, updateEventAdminRequest);
        log.info("Измененное событие: {}", result);

        return result;
    }

    @DeleteMapping("/{eventId}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //204
    public void deleteCommentByAdmin(@PathVariable Long eventId,
                                     @PathVariable Long commentId) {
        eventService.deleteCommentByAdmin(eventId, commentId);
    }
}
