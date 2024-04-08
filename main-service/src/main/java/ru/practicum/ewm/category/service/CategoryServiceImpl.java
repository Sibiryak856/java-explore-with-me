package ru.practicum.ewm.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    public CategoryRepository repository;

    private CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto save(CategoryRequestDto dto) {
        return mapper.toDto(repository.save(mapper.toCategory(dto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto dto) {
        Category updatingCategory = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        updatingCategory.setName(dto.getName());
        return mapper.toDto(repository.save(updatingCategory));
    }

    @Override
    public void delete(Long id) {
        // если нет связанных событий
        repository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return mapper.toDtoList(repository.findAll(pageable));
    }

    @Override
    public CategoryDto getById(long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        return mapper.toDto(category);
    }
}
