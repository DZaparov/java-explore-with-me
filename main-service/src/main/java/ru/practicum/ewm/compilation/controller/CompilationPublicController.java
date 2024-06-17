package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
public class CompilationPublicController {
    public final CompilationService compilationService;

    public CompilationPublicController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> listCompilations(@RequestParam(required = false) boolean pinned,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка получения подборок событий pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> result = compilationService.listCompilations(pinned, from, size);
        log.info("Получены подборки событий: {}", result);

        return result;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Попытка получения подбороки событий compId={}", compId);
        CompilationDto result = compilationService.getCompilation(compId);
        log.info("Получена подборка событий: {}", result);

        return result;
    }
}
