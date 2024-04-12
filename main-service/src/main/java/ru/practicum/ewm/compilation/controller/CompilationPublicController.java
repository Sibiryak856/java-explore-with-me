package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.pagination.MyPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class CompilationPublicController {

    @Autowired
    private CompilationService service;

    @GetMapping
    public List<CompilationDto> getAll(
            @RequestParam(value = "pinned", defaultValue = "true", required = false) Boolean pinned,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Request received: GET /compilations: pinned={}, from={}, size={}", pinned, from, size);
        PageRequest pageRequest = new MyPageRequest(from, size, Sort.unsorted());
        List<CompilationDto> compilations = service.getAll(pinned, pageRequest);
        log.info("Request GET /compilations processed: {}", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable long compId) {
        log.info("Request received: GET /compilations/compId={}", compId);
        CompilationDto compilation = service.getById(compId);
        log.info("Request GET /compilations/compId={} processed: {}", compId, compilation);
        return compilation;
    }
}
