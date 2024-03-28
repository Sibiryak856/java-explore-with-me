package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.StatData;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatData, Long> {

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.appName, s.uri, COUNT(s.ip) as hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "GROUP BY s.appName, s.uri")
    List<ViewStats> findAllByTimeBetween(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         Pageable pageable);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.appName, s.uri, COUNT(DISTINCT s.ip) as hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "GROUP BY s.appName, s.uri")
    List<ViewStats> findAllByTimeBetweenAndUniqueHit(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     Pageable pageable);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.appName, s.uri, COUNT(DISTINCT s.ip) as hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.appName, s.uri")
    List<ViewStats> findAllByTimeBetweenAndUniqueHitAndUriIn(@Param("start") LocalDateTime start,
                                                             @Param("end") LocalDateTime end,
                                                             @Param("uris") List<String> uris,
                                                             Pageable pageable);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.appName, s.uri, COUNT(s.ip) as hits) " +
            "FROM StatData AS s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.appName, s.uri")
    List<ViewStats> findAllByTimeBetweenAndUriIn(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("uris") List<String> uris,
                                                 Pageable pageable);

}
