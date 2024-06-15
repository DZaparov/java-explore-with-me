package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class EventMapper {
    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .state(event.getState())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(event.getConfirmedRequests())
                .paid(event.isPaid())
                .requestModeration(event.isRequestModeration())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(event.getConfirmedRequests())
                .paid(event.isPaid())
                .views(event.getViews())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, User user) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(newEventDto.getLocation())
                .state(EventState.PENDING)
                .participantLimit(newEventDto.getParticipantLimit())
                .confirmedRequests(0)
                .paid(newEventDto.isPaid())
                .requestModeration(newEventDto.isRequestModeration())
                .views(0L)
                .build();
    }

    public static Event toEvent(UpdateEventUserRequest updateEventUserRequest,
                                Event updatedEvent,
                                Category category,
                                EventState state) {
        Event.EventBuilder eventBuilder = Event.builder();

        eventBuilder.id(updatedEvent.getId());
        eventBuilder.createdOn(updatedEvent.getCreatedOn());
        eventBuilder.publishedOn(updatedEvent.getPublishedOn());
        eventBuilder.initiator(updatedEvent.getInitiator());
        eventBuilder.confirmedRequests(updatedEvent.getConfirmedRequests());
        eventBuilder.views(updatedEvent.getViews());

        if (updateEventUserRequest.getTitle() != null) {
            eventBuilder.title(updateEventUserRequest.getTitle());
        } else {
            eventBuilder.title(updatedEvent.getTitle());
        }

        if (updateEventUserRequest.getDescription() != null) {
            eventBuilder.description(updateEventUserRequest.getDescription());
        } else {
            eventBuilder.description(updatedEvent.getDescription());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            eventBuilder.annotation(updateEventUserRequest.getAnnotation());
        } else {
            eventBuilder.annotation(updatedEvent.getAnnotation());
        }

        if (category != null) {
            eventBuilder.category(category);
        } else {
            eventBuilder.category(updatedEvent.getCategory());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            eventBuilder.eventDate(updateEventUserRequest.getEventDate());
        } else {
            eventBuilder.eventDate(updatedEvent.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            eventBuilder.location(updateEventUserRequest.getLocation());
        } else {
            eventBuilder.location(updatedEvent.getLocation());
        }

        eventBuilder.state(state);

        if (updateEventUserRequest.getParticipantLimit() != null) {
            eventBuilder.participantLimit(updateEventUserRequest.getParticipantLimit());
        } else {
            eventBuilder.participantLimit(updatedEvent.getParticipantLimit());
        }

        if (updateEventUserRequest.getPaid() != null) {
            eventBuilder.paid(updateEventUserRequest.getPaid());
        } else {
            eventBuilder.paid(updatedEvent.isPaid());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            eventBuilder.requestModeration(updateEventUserRequest.getRequestModeration());
        } else {
            eventBuilder.requestModeration(updatedEvent.isRequestModeration());
        }

        return eventBuilder.build();
    }

    public static Event toEvent(UpdateEventAdminRequest updateEventAdminRequest,
                                Event updatedEvent,
                                Category category,
                                EventState state) {
        Event.EventBuilder eventBuilder = Event.builder();

        eventBuilder.id(updatedEvent.getId());
        eventBuilder.createdOn(updatedEvent.getCreatedOn());
        eventBuilder.publishedOn(updatedEvent.getPublishedOn());
        eventBuilder.initiator(updatedEvent.getInitiator());
        eventBuilder.confirmedRequests(updatedEvent.getConfirmedRequests());
        eventBuilder.views(updatedEvent.getViews());

        if (updateEventAdminRequest.getTitle() != null) {
            eventBuilder.title(updateEventAdminRequest.getTitle());
        } else {
            eventBuilder.title(updatedEvent.getTitle());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            eventBuilder.description(updateEventAdminRequest.getDescription());
        } else {
            eventBuilder.description(updatedEvent.getDescription());
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            eventBuilder.annotation(updateEventAdminRequest.getAnnotation());
        } else {
            eventBuilder.annotation(updatedEvent.getAnnotation());
        }

        if (category != null) {
            eventBuilder.category(category);
        } else {
            eventBuilder.category(updatedEvent.getCategory());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            eventBuilder.eventDate(updateEventAdminRequest.getEventDate());
        } else {
            eventBuilder.eventDate(updatedEvent.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            eventBuilder.location(updateEventAdminRequest.getLocation());
        } else {
            eventBuilder.location(updatedEvent.getLocation());
        }

        eventBuilder.state(state);

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            eventBuilder.participantLimit(updateEventAdminRequest.getParticipantLimit());
        } else {
            eventBuilder.participantLimit(updatedEvent.getParticipantLimit());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            eventBuilder.paid(updateEventAdminRequest.getPaid());
        } else {
            eventBuilder.paid(updatedEvent.isPaid());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            eventBuilder.requestModeration(updateEventAdminRequest.getRequestModeration());
        } else {
            eventBuilder.requestModeration(updatedEvent.isRequestModeration());
        }

        return eventBuilder.build();
    }
}
