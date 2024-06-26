package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserAdminController {

    private final UserService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto save(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Request received: POST /admin/users: {}", userCreateDto);
        UserDto createdUser = service.save(userCreateDto);
        log.info("Request POST /admin/users processed: user={} is created", createdUser);
        return createdUser;
    }

    @GetMapping
    public List<UserDto> getAll(
            @RequestParam(required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /admin/users: ids={}, from={}, size={}", ids, from, size);
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<UserDto> users = service.getAll(ids, pageRequest);
        log.info("Request GET /admin/users processed: {}", users);
        return users;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Request received: DELETE /admin/users/userId={}", userId);
        service.delete(userId);
        log.info("Request DELETE /admin/users/userId processed");
    }
}
