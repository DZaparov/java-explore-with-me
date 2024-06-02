package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public EndpointHit createHit(EndpointHit endpointHit) {
        return statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (!uris.isEmpty()) {
                return statsRepository.findAllUniqueByTimestampBetweenAndUriIn(start, end, uris);
            } else {
                return statsRepository.findAllUniqueByTimestampBetween(start, end);
            }
        } else {
            if (!uris.isEmpty()) {
                return statsRepository.findAllByTimestampBetweenAndUriIn(start, end, uris);
            } else {
                return statsRepository.findAllByTimestampBetween(start, end);
            }
        }
    }
}
