package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class ParticipationRequestController {
    public final EventService eventService;
    public final ParticipationRequestService participationRequestService;

    public ParticipationRequestController(EventService eventService,
                                          ParticipationRequestService participationRequestService) {
        this.eventService = eventService;
        this.participationRequestService = participationRequestService;
    }

    @GetMapping
    public List<ParticipationRequestDto> listUsersEventRequests(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка получения событий, добавленных текущим пользователем id={}, from={}, size={}",
                userId, from, size);
        List<ParticipationRequestDto> result = participationRequestService.listUsersEventRequests(userId, from, size);
        log.info("Получен список событий. Количество: {}", result.size());

        return result;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable Long userId,
                                                      @RequestParam Long eventId) {
        log.info("Попытка создания запроса на участие пользователя id= {} в событии id={}", userId, eventId);
        ParticipationRequestDto result = participationRequestService.createEventRequest(userId, eventId);
        log.info("Создан запрос на участие: {}", result);

        return result;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequest(@PathVariable Long userId,
                                                      @PathVariable Long requestId) {
        log.info("Попытка отменить запрос id={} на участие пользователя id= {}", requestId, userId);
        ParticipationRequestDto result = participationRequestService.cancelEventRequest(userId, requestId);
        log.info("Запрос отменен: {}", result);

        return result;
    }
}
