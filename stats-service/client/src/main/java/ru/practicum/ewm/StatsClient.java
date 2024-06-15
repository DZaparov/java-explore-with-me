package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(@RequestBody EndpointHit hit) {
        return post("/hit", hit);
    }

    public ResponseEntity<Object> getViewStats(LocalDateTime start,
                                               LocalDateTime end,
                                               List<String> uris,
                                               Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedStart = formatter.format(start);
        String formattedEnd = formatter.format(end);

        Map<String, Object> parameters = Map.of(
                "start", formattedStart,
                "end", formattedEnd,
                "uris", String.join(",", uris),
                "unique", unique
        );

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
