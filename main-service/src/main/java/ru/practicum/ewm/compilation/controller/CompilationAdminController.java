package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    @Autowired
    private CompilationService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Request received: POST /admin/compilations: {}", dto);
        CompilationDto createdCompilation = service.save(dto);
        log.info("Request POST /admin/compilations processed: compilation={} is created", createdCompilation);
        return createdCompilation;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("Request received: DELETE /admin/compilations/compId={}", compId);
        service.delete(compId);
        log.info("Request DELETE /admin/compilations/compId={} processed", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody @Valid UpdateCompilationDto dto) {
        log.info("Request received: PATCH /admin/compilations/compId={}: update: {}", compId, dto);
        CompilationDto updatedCompilation = service.update(compId, dto);
        log.info("Request PATCH /admin/compilations/compId={} processed: compilation={} is updated",
                compId, updatedCompilation);
        return updatedCompilation;
    }
}
