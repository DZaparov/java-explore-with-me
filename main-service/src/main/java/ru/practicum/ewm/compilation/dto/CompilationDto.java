package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@Data
@ToString
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Set<EventShortDto> events;
    private boolean pinned;
}
