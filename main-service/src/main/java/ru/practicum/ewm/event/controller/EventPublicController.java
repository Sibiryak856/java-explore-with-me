package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.pagination.MyPageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.EwmApp.CLIENT;
import static ru.practicum.ewm.EwmApp.DATE_FORMAT;
import static ru.practicum.ewm.event.controller.SortQuery.EVENT_DATE;

@RestController
@Validated
@Slf4j
@RequestMapping("/events")
public class EventPublicController {

    private final EventService service;

    @Autowired
    public EventPublicController(EventService service) {
        this.service = service;
    }


    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(value = "rangeStart", defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", defaultValue = "#{T(java.time.LocalDateTime).MAX}", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            HttpServletRequest request) {
        log.info("Request received: GET /events: " +
                        "text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                        "sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        PageRequest pageRequest = new MyPageRequest(from, size, Sort.unsorted());
        if (sort != null && sort.equals(EVENT_DATE.toString())) {
            pageRequest.withSort(Sort.Direction.ASC, "eventDate");
        }
        List<EventShortDto> shortDtoList = service.getAllPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageRequest);
        log.info("Request GET /admin/events processed:{}", shortDtoList);
        StatDataCreateDto createDto = StatDataCreateDto.builder()
                .appName("ewm-main.service")
                .ip(request.getRemoteAddr())
                .created(request.getRequestURI())
                .build();
        CLIENT.postStat(createDto);
        log.info("Request data sent to stat-server: {}", createDto);
        return shortDtoList;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable long id,
                                HttpServletRequest request) {
        log.info("Request received: GET /events/id={} from ip={}", id, request.getRemoteAddr());
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        EventFullDto fullDto = service.getById(id);
        log.info("Request GET /events/id={} processed:{}", id, fullDto);
        StatDataCreateDto createDto = StatDataCreateDto.builder()
                .appName("ewm-main.service")
                .ip(request.getRemoteAddr())
                .created(request.getRequestURI())
                .build();
        CLIENT.postStat(createDto);
        log.info("Request data sent to stat-server: {}", createDto);
        return fullDto;
    }

}