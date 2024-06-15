package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.ewm.event.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private LocalDateTime created;
    private ParticipationRequestStatus status;
}
