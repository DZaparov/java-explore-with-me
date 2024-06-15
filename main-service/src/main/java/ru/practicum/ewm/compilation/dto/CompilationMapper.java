package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, HashSet<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .events(events)
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        Set<EventShortDto> eventsShortDto = null;

        if (compilation.getEvents() != null) {
            eventsShortDto = compilation.getEvents()
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toSet());
        }

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(eventsShortDto)
                .pinned(compilation.getPinned())
                .build();
    }

    public static Compilation toCompilation(Compilation updatedCompilation,
                                            UpdateCompilationRequest updateCompilationRequest,
                                            Set<Event> events) {
        Compilation.CompilationBuilder compilationBuilder = Compilation.builder();

        compilationBuilder.id(updatedCompilation.getId());

        if (updateCompilationRequest.getTitle() != null) {
            compilationBuilder.title(updateCompilationRequest.getTitle());
        } else {
            compilationBuilder.title(updatedCompilation.getTitle());
        }

        if (updateCompilationRequest.getEvents() != null) {
            compilationBuilder.events(events);
        } else {
            compilationBuilder.events(updatedCompilation.getEvents());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilationBuilder.pinned(updateCompilationRequest.getPinned());
        } else {
            compilationBuilder.pinned(updatedCompilation.getPinned());
        }

        return compilationBuilder.build();
    }
}
