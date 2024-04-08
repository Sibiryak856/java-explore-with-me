package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.user.repository.UserRepository;

@Service
public class EventServiceImpl {

    public EventRepository repository;
    private EventMapper mapper;
    private LocationMapper locationMapper;
    private LocationRepository locationRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public EventServiceImpl(EventRepository repository, EventMapper mapper, LocationMapper locationMapper, LocationRepository locationRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }



}
