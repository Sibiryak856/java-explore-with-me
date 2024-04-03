package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.model.StatData;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatData, Long> {

    @Query("SELECT new ru.practicum.ewm.ViewStatDto(s.appName, s.uri, COUNT(s.ip) AS hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN (:start) AND (:end) " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStatDto> findAllByTimeBetween(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStatDto(s.appName, s.uri, COUNT(DISTINCT s.ip) AS hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN (:start) AND (:end) " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStatDto> findAllUniqueHitByTimeBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("uris") List<String> uris);
}
