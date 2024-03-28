package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.StatDataResponseDto;
import ru.practicum.ewm.service.StatService;

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
    public StatDataResponseDto save(@RequestBody @Valid StatDataCreateDto statData) {
        log.info("Request received: POST /hit: {}", statData);
        StatDataResponseDto createdStatData = statService.save(statData);
        log.info("Request POST /hit processed: {}", createdStatData);
        return createdStatData;
    }

    @GetMapping("/stats")
    public List<ViewStatDto> getHits(@RequestParam("start")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime start,
                                     @RequestParam("end")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Request received: GET /stats");
        List<ViewStatDto> statDataList = statService.getHits(
                start, end, uris, unique);
        log.info("Request received: GET /stats processed: {}", statDataList);
        return statDataList;
    }
}
