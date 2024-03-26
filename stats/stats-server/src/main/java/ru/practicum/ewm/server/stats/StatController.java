package ru.practicum.ewm.server.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.stats.StatDataCreateDto;
import ru.practicum.ewm.server.stats.service.StatService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
public class StatController {

    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void save(@RequestBody @Valid StatDataCreateDto statData) {
        log.info("Request received: POST /hit: {}", statData);
        statService.save(statData);
        log.info("Request POST /hit processed successfully");
    }


}
