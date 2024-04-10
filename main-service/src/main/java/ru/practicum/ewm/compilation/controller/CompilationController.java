package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.pagination.MyPageRequest;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/compilations")
public class CompilationController {

    @Autowired
    private CompilationService service;

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Request received: POST /admin/compilations: {}", dto);
        CompilationDto createdCompilation = service.save(dto);
        log.info("Request POST /admin/compilations processed: compilation={} is created",
                createdCompilation);
        return createdCompilation;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("Request received: DELETE /admin/compilations/compId={}", compId);
        service.delete(compId);
        log.info("Request DELETE /admin/compilations/compId={} processed", compId);
    }

    @Transactional
    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable long compId,
                                 @RequestBody @Valid NewCompilationDto dto) {
        log.info("Request received: PATCH /admin/compilations/compId={}", compId);
        CompilationDto updatedCompilation = service.update(compId, dto);
        log.info("Request PATCH /admin/compilations/compId={} processed: compilation={} is updated",
                compId, updatedCompilation);
        return updatedCompilation;
    }

    @GetMapping
    public List<CompilationDto> getAll(
            @RequestParam Boolean pinned,
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
