package ru.practicum.ewm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.model.StatData;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StatRepositoryTest {

    @Autowired
    private StatRepository repository;

    private StatData statData1;
    private StatData statData2;
    private StatData statData3;
    private StatData statData4;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);
        statData1 = StatData.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .created(now)
                .build();
        statData2 = StatData.builder()
                .appName("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.1")
                .created(now)
                .build();
        statData3 = StatData.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("292.163.0.1")
                .created(now)
                .build();
        statData4 = StatData.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("292.163.0.1")
                .created(now.plusSeconds(5))
                .build();
        repository.save(statData1);
        repository.save(statData2);
        repository.save(statData3);
        repository.save(statData4);
    }

    @Test
    void findAllByTimeBetweenAndUriInTest() {
        List<ViewStatDto> result = repository.findAllByTimeBetween(
                now.minusHours(1),
                now.plusHours(1),
                List.of("/events/1"));

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getHits()).isEqualTo(3);
    }

    @Test
    void findAllByTimeBetweenAndUriIsNullTest() {
        List<ViewStatDto> result = repository.findAllByTimeBetween(
                now.minusHours(1),
                now.plusHours(1),
                null);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getHits()).isEqualTo(3);
        assertThat(result.get(1).getHits()).isEqualTo(1);
    }

    @Test
    void findAllByTimeBetween_whenTimeInFuture_thenReturnEmptyList() {
        List<ViewStatDto> result = repository.findAllByTimeBetween(
                now.minusHours(1),
                now.minusMinutes(30),
                List.of("/events/1"));

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void findAllUniqueHitByTimeBetweenAndUrisIsNullTest() {
        List<ViewStatDto> result = repository.findAllUniqueHitByTimeBetween(
                now.minusHours(1),
                now.plusHours(1),
                null);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getHits()).isEqualTo(2);
        assertThat(result.get(1).getHits()).isEqualTo(1);
    }

    @Test
    void findAllUniqueHitByTimeBetweenTest() {
        List<ViewStatDto> result = repository.findAllUniqueHitByTimeBetween(
                now.minusHours(1),
                now.plusHours(1),
                List.of("/events/1"));

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getHits()).isEqualTo(2);
    }
}