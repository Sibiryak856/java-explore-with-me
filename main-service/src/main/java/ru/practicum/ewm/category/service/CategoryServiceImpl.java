package ru.practicum.ewm.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    public CategoryRepository categoryRepository;
    private EventRepository eventRepository;

    private CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, EventRepository eventRepository, CategoryMapper mapper) {
        this.categoryRepository = repository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto save(CategoryRequestDto dto) {
        return mapper.toDto(
                categoryRepository.save(
                        mapper.toCategory(dto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto dto) {
        Category updatingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
        updatingCategory.setName(dto.getName());
        return mapper.toDto(categoryRepository.save(updatingCategory));
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", id));
        }
        if (!eventRepository.findAllByCategoryId(id)
                .isEmpty()) {
            throw new NotAccessException("The category is not empty");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return mapper.toDtoList(categoryRepository.findAll(pageable));
    }

    @Override
    public CategoryDto getById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        return mapper.toDto(category);
    }
}
