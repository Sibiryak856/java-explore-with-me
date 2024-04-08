package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int offset,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int limit) {
        log.info("Request received: GET /categories: offset={}, limit={}", offset, limit);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<CategoryDto> categories = service.getAll(pageable);
        log.info("Request GET /categories processed: {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@RequestHeader long catId) {
        log.info("Request received: GET /categories/id={}", catId);
        CategoryDto categoryDto = service.getById(catId);
        log.info("Request GET /categories/id={} processed: {}", categoryDto);
        return categoryDto;
    }

}
