package ru.practicum.ewm.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.EwmApp.CLIENT;
import static ru.practicum.ewm.EwmApp.FORMATTER;

@Service
public class CompilationServiceImpl implements CompilationService {

    public CompilationRepository compilationRepository;

    private CompilationMapper compilationMapper;

    private EventRepository eventRepository;

    private EventMapper eventMapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper, EventRepository eventRepository, EventMapper eventMapper) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public CompilationDto save(NewCompilationDto dto) {
        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
        if (events.size() < dto.getEvents().size()) {
            throw new NotFoundException("Not all events is present");
        }
        Compilation compilation = compilationRepository.save(
                compilationMapper.toCompilation(dto));
        List<ViewStatDto> viewStatList = new ArrayList<>();
        if (!events.isEmpty()) {
            List<String> eventsUri = events.stream()
                    .map(event -> String.format("/events/%d", event.getId()))
                    .collect(Collectors.toList());
            viewStatList = CLIENT.getStats(
                    LocalDateTime.MIN.format(FORMATTER),
                    LocalDateTime.MAX.format(FORMATTER),
                    eventsUri,
                    Boolean.FALSE);
        }
        return compilationMapper.toDto(compilation, eventMapper.toEventShortDtoList(events, viewStatList));
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
        List<ViewStatDto> viewStatList = new ArrayList<>();
        if (!events.isEmpty()) {
            List<String> eventsUri = events.stream()
                    .map(event -> String.format("/events/%d", event.getId()))
                    .collect(Collectors.toList());
            viewStatList = CLIENT.getStats(
                    LocalDateTime.MIN.format(FORMATTER),
                    LocalDateTime.MAX.format(FORMATTER),
                    eventsUri,
                    Boolean.FALSE);
        }
        return compilationMapper.toDto(updated, eventMapper.toEventShortDtoList(events, viewStatList));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        Set<Long> eventIds = new HashSet<>();
        compilations.forEach(
                compilation -> eventIds.addAll(compilation.getEvents())
        );

        List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
        List<ViewStatDto> viewStatList = new ArrayList<>();
        if (!events.isEmpty()) {
            List<String> eventsUri = eventIds.stream()
                    .map(id -> String.format("/events/%d", id))
                    .collect(Collectors.toList());
            viewStatList = CLIENT.getStats(
                    LocalDateTime.MIN.format(FORMATTER),
                    LocalDateTime.MAX.format(FORMATTER),
                    eventsUri,
                    Boolean.FALSE);
        }
        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(events, viewStatList);
        Map<Long, EventShortDto> eventMap = new HashMap<>();
        eventShortDtoList.forEach(dto -> eventMap.put(dto.getId(), dto));
        //Map<Compilation, List<EventShortDto>> map = new HashMap<>();
        List<CompilationDto> dtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<EventShortDto> list = new ArrayList<>();
            compilation.getEvents().forEach(id ->
                    list.add(eventMap.get(id)));
            //map.put(compilation, list);
            dtos.add(compilationMapper.toDto(compilation, list));
        }
        return dtos;
    }

    @Override
    public CompilationDto getById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compId)));
        List<Event> events = eventRepository.findAllByIdIn(compilation.getEvents());
        List<ViewStatDto> viewStatList = new ArrayList<>();
        if (!events.isEmpty()) {
            List<String> eventsUri = events.stream()
                    .map(event -> String.format("/events/%d", event.getId()))
                    .collect(Collectors.toList());
            viewStatList = CLIENT.getStats(
                    LocalDateTime.MIN.format(FORMATTER),
                    LocalDateTime.MAX.format(FORMATTER),
                    eventsUri,
                    Boolean.FALSE);
        }
        return compilationMapper.toDto(compilation, eventMapper.toEventShortDtoList(events, viewStatList));
    }
}
