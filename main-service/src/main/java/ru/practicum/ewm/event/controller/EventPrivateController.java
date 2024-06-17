package ru.practicum.ewm.event.controller;

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
public class EventPrivateController {
    public final EventService eventService;
    public final LocationService locationService;

    public EventPrivateController(EventService eventService, LocationService locationService) {
        this.eventService = eventService;
        this.locationService = locationService;
    }

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
}
