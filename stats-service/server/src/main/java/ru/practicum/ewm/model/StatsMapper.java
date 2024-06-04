package ru.practicum.ewm.model;

import ru.practicum.ewm.EndpointHit;

public class StatsMapper {
    public static EndpointHit toEndpointHit(Stats stats) {
        return new EndpointHit(
                stats.getId(),
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp()
        );
    }

    public static Stats toStats(EndpointHit endpointHit) {
        return new Stats(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
