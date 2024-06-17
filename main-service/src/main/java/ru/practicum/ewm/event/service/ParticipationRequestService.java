package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> listUsersEventRequests(Long userId, int from, int size);

    ParticipationRequestDto createEventRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelEventRequest(Long userId, Long requestId);
}
