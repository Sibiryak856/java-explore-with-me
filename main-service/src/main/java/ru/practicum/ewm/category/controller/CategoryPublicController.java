package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /categories: from={}, size={}", from, size);
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<CategoryDto> categories = service.getAll(pageRequest);
        log.info("Request GET /categories processed: {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable long catId) {
        log.info("Request received: GET /categories/id={}", catId);
        CategoryDto categoryDto = service.getById(catId);
        log.info("Request GET /categories/id={} processed: {}", catId, categoryDto);
        return categoryDto;
    }

}
