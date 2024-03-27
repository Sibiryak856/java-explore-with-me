package ru.practicum.ewm.server.stats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.stats.StatDataCreateDto;
import ru.practicum.ewm.dto.stats.StatDataDto;
import ru.practicum.ewm.server.stats.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
        log.info("Request POST /hit successfully processed");
    }

    @GetMapping("/stats")
    public List<StatDataDto> getHits(@RequestParam LocalDateTime start,
                                     @RequestParam LocalDateTime end,
                                     @RequestParam(required = false, defaultValue = "Collections.emptyList()")
                                         List<String> uris,
                                     @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Request received: GET /stats");
        List<StatDataDto> statDataList = statService.getHits(start, end, uris, unique);
        log.info("Request received: GET /stats processed: {}", statDataList);
        return statDataList;
    }
}
