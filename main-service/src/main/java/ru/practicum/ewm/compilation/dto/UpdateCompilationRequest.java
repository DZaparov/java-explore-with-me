package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    private Set<Long> events;
    private Boolean pinned;
}
