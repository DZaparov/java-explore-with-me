package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.ParticipationRequestRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    public final StatsClient statsClient;

    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            ParticipationRequestRepository participationRequestRepository,
                            StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.participationRequestRepository = participationRequestRepository;
        this.statsClient = statsClient;
    }

    @Override
    public List<EventShortDto> listUsersEvents(Long userId, int from, int size) {
        checkAdnGetUser(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<Event> events = eventRepository.findAllByInitiatorId(userId, page);

        return events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = checkAdnGetUser(userId);
        Category category = checkAdnGetCategory(newEventDto.getCategory());

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " +
                            newEventDto.getEventDate());
        }

        Event event = EventMapper.toEvent(newEventDto, category, user);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto listUsersEventFull(Long userId, Long eventId) {
        User user = checkAdnGetUser(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkAdnGetUser(userId);

        Category category = null;
        if (updateEventUserRequest.getCategory() != null) {
            category = checkAdnGetCategory(updateEventUserRequest.getCategory());
        }

        Event updatedEvent = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updatedEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event must not be published");
        }

        if (updatedEvent.getState().equals(EventState.CANCELED) ||
                updatedEvent.getState().equals(EventState.PENDING)) {
            if (updateEventUserRequest.getEventDate() != null &&
                    updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(
                        "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " +
                                updateEventUserRequest.getEventDate());
            }

            EventState state = EventState.PENDING;

            if (updateEventUserRequest.getStateAction() != null) {
                if (updateEventUserRequest.getStateAction().equals(EventStateUserAction.CANCEL_REVIEW)) {
                    state = EventState.CANCELED;
                } else if (updateEventUserRequest.getStateAction().equals(EventStateUserAction.SEND_TO_REVIEW)) {
                    state = EventState.PENDING;
                }
            }

            Event event = EventMapper.toEvent(updateEventUserRequest, updatedEvent, category, state);
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
    }

    @Override
    public List<ParticipationRequestDto> listUsersEventRequests(Long userId, Long eventId) {
        checkAdnGetUser(userId);
        checkAdnGetEvent(eventId);

        List<ParticipationRequest> participationRequest =
                participationRequestRepository.findByEventId(eventId);

        return participationRequest
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        checkAdnGetUser(userId);
        checkAdnGetEvent(eventId);

        Event event = checkAdnGetEvent(eventId);
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
            if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
                //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
                if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                    throw new ConflictException("The participant limit has been reached");
                }
                //проверяем это требование:
                //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
                //получаем список заявок со статусом не PENDING
                List<ParticipationRequest> checkParticipationRequests =
                        participationRequestRepository.findAllByIdInAndStatusNot(
                                eventRequestStatusUpdateRequest.getRequestIds(), ParticipationRequestStatus.PENDING);
                if (!checkParticipationRequests.isEmpty()) {
                    throw new ConflictException("Request must have status PENDING");
                }

                //количество заявок, которые нужно одобрить
                int confirmedNumbers = event.getParticipantLimit() - event.getConfirmedRequests();
                log.info("confirmedNumbers=" + confirmedNumbers);
                //получаем список заявок, которые подтверждаем
                List<Long> confirmedRequestIds = participationRequestRepository.getRequestsToConfirm(
                        eventRequestStatusUpdateRequest.getRequestIds(),
                        confirmedNumbers);

                //остальные отклоняем
                List<Long> rejectedRequestIds = participationRequestRepository.getRequestsToReject(
                        eventRequestStatusUpdateRequest.getRequestIds(),
                        confirmedNumbers);

                log.info("confirmedRequestIds=" + confirmedRequestIds);
                log.info("rejectedRequestIds=" + rejectedRequestIds);

                participationRequestRepository.updateEventRequest(
                        confirmedRequestIds,
                        ParticipationRequestStatus.CONFIRMED);

                participationRequestRepository.updateEventRequest(
                        rejectedRequestIds,
                        ParticipationRequestStatus.REJECTED);


                confirmedRequests = participationRequestRepository.findAllByIdIn(confirmedRequestIds);
                rejectedRequests = participationRequestRepository.findAllByIdIn(rejectedRequestIds);

                event.setConfirmedRequests(confirmedRequests.size());
                eventRepository.save(event);

            }


        } else {
            //проверяем на статус REJECTED
            List<ParticipationRequest> checkParticipationRequests =
                    participationRequestRepository.findAllByIdInAndStatusNot(
                            eventRequestStatusUpdateRequest.getRequestIds(), ParticipationRequestStatus.PENDING);
            if (!checkParticipationRequests.isEmpty()) {
                throw new ConflictException("Request must have status PENDING");
            }

            participationRequestRepository.updateEventRequest(
                    eventRequestStatusUpdateRequest.getRequestIds(),
                    ParticipationRequestStatus.REJECTED);

            rejectedRequests =
                    participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        }
        return new EventRequestStatusUpdateResult(
                confirmedRequests
                        .stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejectedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<EventFullDto> listEventsAdminFilter(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();

        if (userIds != null && !userIds.isEmpty()) {
            predicate = predicate.and(qEvent.initiator.id.in(userIds));
        }

        if (states != null && !states.isEmpty()) {
            predicate = predicate.and(qEvent.state.in(states));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicate = predicate.and(qEvent.category.id.in(categoryIds));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicate = predicate.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        }

        Page<Event> events = eventRepository.findAll(predicate, page);

        return events
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        checkAdnGetEvent(eventId);

        Category category = null;

        if (updateEventAdminRequest.getCategory() != null) {
            category = checkAdnGetCategory(updateEventAdminRequest.getCategory());
        }

        Event updatedEvent = checkAdnGetEvent(eventId);

        EventState state = EventState.PENDING;

        if (updateEventAdminRequest.getStateAction() != null) {

            if (updateEventAdminRequest.getStateAction().equals(EventStateAdminAction.PUBLISH_EVENT)) {
                if (!updatedEvent.getState().equals(EventState.PENDING)) {
                    throw new ConflictException(
                            "Cannot publish the event because it's not in the right state: PUBLISHED");
                }
            }

            if (updateEventAdminRequest.getStateAction().equals(EventStateAdminAction.REJECT_EVENT)) {
                if (updatedEvent.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException(
                            "Cannot reject the event because it's not in the right state: not PUBLISHED");
                }
            }

            if (updateEventAdminRequest.getStateAction().equals(EventStateAdminAction.REJECT_EVENT)) {
                state = EventState.CANCELED;
            } else if (updateEventAdminRequest.getStateAction().equals(EventStateAdminAction.PUBLISH_EVENT)) {
                state = EventState.PUBLISHED;
            }

            if (state.equals(EventState.PUBLISHED)) {
                if (updatedEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException(
                            "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                }
            }
        }
        Event event = EventMapper.toEvent(updateEventAdminRequest, updatedEvent, category, state);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> listEventsPublicFilter(String text, List<Long> categoryIds, Boolean paid,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable, String sort, int from, int size,
                                                      HttpServletRequest request) {
        statsClient.createHit(EndpointHit.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Sort sorting = Sort.by(Sort.Order.asc("id"));

        if ("EVENT_DATE".equals(sort)) {
            sorting = Sort.by(Sort.Order.asc("eventDate"));
        } else if ("VIEWS".equals(sort)) {
            sorting = Sort.by(Sort.Order.desc("views"));
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sorting);

        QEvent qEvent = QEvent.event;
        BooleanExpression predicate = qEvent.isNotNull();

        if (text != null && !text.isBlank()) {
            predicate = predicate.and(qEvent.annotation.toLowerCase().like(text.toLowerCase())
                    .or(qEvent.description.toLowerCase().like(text.toLowerCase())));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicate = predicate.and(qEvent.category.id.in(categoryIds));
        }

        if (paid != null) {
            predicate = predicate.and(QEvent.event.paid.eq(paid));
        }

        if (rangeStart != null && rangeEnd != null) {
            predicate = predicate.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        } else {
            predicate = predicate.and(qEvent.eventDate.after(LocalDateTime.now()));
        }

        predicate = predicate.and(QEvent.event.state.eq(EventState.PUBLISHED));

        if (onlyAvailable != null) {
            if (onlyAvailable) {
                predicate.and(QEvent.event.participantLimit.gt(
                        JPAExpressions
                                .select(QParticipationRequest.participationRequest.count())
                                .from(QParticipationRequest.participationRequest)
                                .where(QParticipationRequest.participationRequest.event.id.eq(QEvent.event.id))));
            }
        }

        Page<Event> events = eventRepository.findAll(predicate, page);

        ResponseEntity<Object> responseEntity = statsClient.getViewStats(
                findEarliestDate(events.toList()),
                LocalDateTime.now(),
                Collections.singletonList(request.getRequestURI()),
                true);

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> viewStatsList = mapper.convertValue(
                responseEntity.getBody(),
                new TypeReference<List<ViewStats>>() {
                });

        Map<Long, Long> eventIdHits = new HashMap<>();

        for (ViewStats viewStats : viewStatsList) {
            eventIdHits.put(Long.parseLong(viewStats.getUri().split("/")[2]), viewStats.getHits());
        }

        for (Event event : events) {
            event.setViews(eventIdHits.get(event.getId()));
        }

        return events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventPublicFull(Long eventId, HttpServletRequest request) {
        Event event = checkAdnGetEvent(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {

            statsClient.createHit(EndpointHit.builder()
                    .app("ewm-main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());

            ResponseEntity<Object> responseEntity = statsClient.getViewStats(
                    event.getCreatedOn(),
                    LocalDateTime.now(),
                    Collections.singletonList(request.getRequestURI()),
                    true);

            ObjectMapper mapper = new ObjectMapper();
            List<ViewStats> viewStatsList = mapper.convertValue(
                    responseEntity.getBody(),
                    new TypeReference<List<ViewStats>>() {
                    });

            event.setViews(viewStatsList.get(0).getHits());

            return EventMapper.toEventFullDto(event);
        } else {
            throw new NotFoundException("You cannot look at an unpublished event");
        }

    }

    private User checkAdnGetUser(Long userId) {
        if (userId != null) {
            return userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("User with id=" + userId + " was not found"));
        }
        return null;
    }

    private Event checkAdnGetEvent(Long eventId) {
        if (eventId != null) {
            return eventRepository.findById(eventId).orElseThrow(() ->
                    new NotFoundException("Event with id=" + eventId + " was not found"));
        }
        return null;
    }

    private Category checkAdnGetCategory(Long categoryId) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId).orElseThrow(() ->
                    new NotFoundException("Category with id=" + categoryId + " was not found"));
        }
        return null;
    }

    public LocalDateTime findEarliestDate(List<Event> events) {
        return events.stream()
                .map(Event::getEventDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }
}
