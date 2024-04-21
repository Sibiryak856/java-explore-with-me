package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService service;

    @Autowired
    public CategoryAdminController(CategoryService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto save(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Request received: POST /admin/categories: {}", categoryRequestDto);
        CategoryDto createdCategory = service.save(categoryRequestDto);
        log.info("Request POST /admin/categories processed: category={} is created", createdCategory);
        return createdCategory;
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        log.info("Request received: PATCH /admin/categories/{}: {}",catId, categoryRequestDto);
        CategoryDto updatedCategory = service.update(catId, categoryRequestDto);
        log.info("Request PATCH /admin/categories processed: category={} is updated", updatedCategory);
        return updatedCategory;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void delete(@PathVariable Long catId) {
        log.info("Request received: DELETE /admin/categories/id={}", catId);
        service.delete(catId);
        log.info("Request DELETE /admin/categories/id={} processed", catId);
    }

}
