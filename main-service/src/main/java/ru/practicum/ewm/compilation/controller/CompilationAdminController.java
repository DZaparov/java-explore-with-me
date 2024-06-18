package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@AllArgsConstructor
public class CompilationAdminController {
    public final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Попытка создания подборки событий {}", newCompilationDto);
        CompilationDto result = compilationService.createCompilation(newCompilationDto);
        log.info("Создана подборка событий: {}", result);

        return result;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //204
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Попытка удаления подборки событий id={}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Удалена подборка событий");
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Попытка изменения подборки событий id={}: {}", compId, updateCompilationRequest);
        CompilationDto result = compilationService.updateCompilation(compId, updateCompilationRequest);
        log.info("Измененная подборка событий: {}", result);

        return result;
    }
}
