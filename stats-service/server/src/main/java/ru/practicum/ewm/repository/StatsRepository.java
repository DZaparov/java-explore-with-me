package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp <= ?2 " +
            "AND s.uri in ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.uri) DESC")
    List<ViewStats> findAllUniqueByTimestampBetweenAndUriIn(
            LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp <= ?2 " +
            "AND s.uri in ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.uri) DESC")
    List<ViewStats> findAllByTimestampBetweenAndUriIn(
            LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp <= ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.uri) DESC")
    List<ViewStats> findAllUniqueByTimestampBetween(
            LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp <= ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.uri) DESC")
    List<ViewStats> findAllByTimestampBetween(
            LocalDateTime start, LocalDateTime end);
}
