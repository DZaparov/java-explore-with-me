package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> listUsersEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto listUsersEventFull(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> listUsersEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> listEventsAdminFilter(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> listEventsPublicFilter(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               String uri, String ip);

    EventFullDto getEventPublicFull(Long eventId, String uri, String ip);

    CommentDto addComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    void deleteComment(Long userId, Long eventId, Long commentId);

    CommentDto updateComment(UpdateCommentRequest updateCommentRequest, Long userId, Long eventId, Long commentId);

    void deleteCommentByAdmin(Long eventId, Long commentId);

    CommentDto getCommentById(Long commentId);
}
