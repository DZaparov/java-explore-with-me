package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.event.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getCreated(),
                participationRequest.getStatus()
        );
    }
}
