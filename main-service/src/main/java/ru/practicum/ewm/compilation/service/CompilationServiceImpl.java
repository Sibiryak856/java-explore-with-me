package ru.practicum.ewm.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventServiceImpl;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.*;

@Service
public class CompilationServiceImpl implements CompilationService {

    public CompilationRepository compilationRepository;

    private CompilationMapper compilationMapper;

    private EventRepository eventRepository;

    private RequestRepository requestRepository;
    private EventMapper eventMapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper, EventRepository eventRepository, RequestRepository requestRepository, EventMapper eventMapper) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        if (events.size() < newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Not all events is present");
        }
        Compilation compilation = compilationRepository.save(
                compilationMapper.toCompilation(newCompilationDto));

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(events);

        return compilationMapper.toDto(compilation,
                eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap));
    }

    @Override
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(
                    String.format("Compilation with id=%d was not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(long compId, NewCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));
        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
        if (events.size() < dto.getEvents().size()) {
            throw new NotFoundException("Not all events is present");
        }
        Compilation updated = compilationRepository.save(
                compilationMapper.update(dto, compilation));

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(events);

        return compilationMapper.toDto(updated,
                eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        Set<Event> events = new HashSet<>();
        compilations.forEach(compilation -> events.addAll(compilation.getEvents()));

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.copyOf(events));

        Map<Long, EventShortDto> eventShortDtosMap = eventMapper.toEventShortDtosMap(
                List.copyOf(events), viewStatMap);

        List<CompilationDto> compilationDtos = new ArrayList<>();
        compilations.forEach(compilation -> {
            List<EventShortDto> list = new ArrayList<>();
            compilation.getEvents().forEach(event ->
                    list.add(eventShortDtosMap.get(event.getId())));
            list.sort((e1, e2) ->
                    e2.getViews().compareTo(e1.getViews()));
            compilationDtos.add(compilationMapper.toDto(compilation, list));
        });
        return compilationDtos;
    }

    @Override
    public CompilationDto getById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));
        List<Event> events = List.copyOf(compilation.getEvents());

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.copyOf(events));

        return compilationMapper.toDto(compilation,
                eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap));
    }
}
