package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.service.LocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@AllArgsConstructor
public class EventPrivateController {
    public final EventService eventService;
    public final LocationService locationService;

    @GetMapping
    public List<EventShortDto> listUsersEvents(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка получения событий, добавленных текущим пользователем id={}, from={}, size={}",
                userId, from, size);
        List<EventShortDto> result = eventService.listUsersEvents(userId, from, size);
        log.info("Получен список событий: {}", result);

        return result;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Попытка создания события {}", newEventDto);
        Location location = locationService.createLocation(newEventDto.getLocation());
        EventFullDto result = eventService.createEvent(userId, newEventDto);
        log.info("Создано событие: {}", result);

        return result;
    }

    @GetMapping("/{eventId}")
    public EventFullDto listUsersEventFull(@PathVariable Long userId,
                                           @PathVariable Long eventId) {
        log.info("Попытка получения полной информации о событии id={} добавленном текущим пользователем id={}",
                eventId, userId);
        EventFullDto result = eventService.listUsersEventFull(userId, eventId);
        log.info("Получено событие: {}", result);

        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Попытка изменения события id={} добавленного текущим пользователем id={}, {}",
                eventId, userId, updateEventUserRequest);
        EventFullDto result = eventService.updateEvent(userId, eventId, updateEventUserRequest);
        log.info("Измененное событие: {}", result);

        return result;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> listUsersEventRequests(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        log.info("Попытка получения информации о запросах на участие в событии id={} текущего пользователя id={}",
                eventId, userId);
        List<ParticipationRequestDto> result = eventService.listUsersEventRequests(userId, eventId);
        log.info("Получен список событий: {}", result);

        return result;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Попытка изменения статуса заявок на участие в событии id={} текущего пользователя id={}, {}",
                eventId, userId, eventRequestStatusUpdateRequest);
        EventRequestStatusUpdateResult result = eventService.updateEventRequestStatus(
                userId,
                eventId,
                eventRequestStatusUpdateRequest);
        log.info("Измененное событие: {}", result);

        return result;
    }

    @PostMapping("/{eventId}/comment")
    @ResponseStatus(code = HttpStatus.CREATED) //201
    public CommentDto addComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                 @PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventService.addComment(newCommentDto, userId, eventId);
    }

    @DeleteMapping("/{eventId}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //204
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        eventService.deleteComment(userId, eventId, commentId);
    }

    @PatchMapping("/{eventId}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.OK) //200
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentRequest updateCommentRequest,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId) {
        log.info("Попытка обновить комментарий с id={} на {}", commentId, updateCommentRequest);
        CommentDto result = eventService.updateComment(updateCommentRequest, userId, eventId, commentId);
        log.info("Обновлен комментарий: {}", result);

        return result;
    }

    @GetMapping("/{eventId}/comment/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Попытка получения комментария id=: {}", commentId);
        CommentDto result = eventService.getCommentById(commentId);
        log.info("Комментарий получен: {}", result);

        return result;
    }
}
