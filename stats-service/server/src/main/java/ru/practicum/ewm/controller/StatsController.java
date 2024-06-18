package ru.practicum.ewm.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class StatsController {
    public final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EndpointHit createHit(@RequestBody EndpointHit hit) {
        log.info("Сохранение информации о запросе: {}", hit);
        EndpointHit result = statsService.createHit(hit);
        log.info("Информация сохранена: {}", result);
        return result;
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получение статистики по посещениям start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (uris == null) {
            uris = new ArrayList<>();
        }
        List<ViewStats> result = statsService.getViewStats(start, end, uris, unique);
        log.info("Информация получена: {}", result);
        return result;
    }
}
