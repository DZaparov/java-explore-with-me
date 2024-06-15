package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.model.EventStateUserAction;
import ru.practicum.ewm.location.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventUserRequest {
    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 7000)
    private String description;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;

    private Location location;

    private EventStateUserAction stateAction;

    @Positive
    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;
}
