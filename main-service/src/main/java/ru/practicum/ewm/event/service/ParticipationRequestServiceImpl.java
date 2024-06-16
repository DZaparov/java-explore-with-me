package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.ParticipationRequestRepository;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.dto.ParticipationRequestMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.ParticipationRequest;
import ru.practicum.ewm.event.model.ParticipationRequestStatus;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;


    public ParticipationRequestServiceImpl(EventRepository eventRepository,
                                           UserRepository userRepository,
                                           CategoryRepository categoryRepository,
                                           ParticipationRequestRepository participationRequestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.participationRequestRepository = participationRequestRepository;
    }

    @Override
    public List<ParticipationRequestDto> listUsersEventRequests(Long userId, int from, int size) {
        applyChecks(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<ParticipationRequest> participationRequests =
                participationRequestRepository.findAllByRequesterId(userId, page);

        return participationRequests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        if (participationRequestRepository.existsByRequesterIdAndEventId(user.getId(), event.getId())) {
            throw new ConflictException("Request exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("The event initiator cannot add a request to participate in his event");
        }

        if (event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("You cannot participate in an unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new ConflictException("The event has reached the limit of requests for participation");
            }
        }

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(ParticipationRequestStatus.PENDING)
                .build();

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        eventRepository.save(event);

        return ParticipationRequestMapper.toParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        applyChecks(userId);

        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request with id=" + requestId + " was not found"));

        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);

        return ParticipationRequestMapper.toParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
    }

    private User applyChecks(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }
}
