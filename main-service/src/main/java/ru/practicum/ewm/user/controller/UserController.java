package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService service;

    @Transactional
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
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int offset,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int limit) {
        log.info("Request received: GET /admin/users: ids={}, offset={}, limit={}", ids, offset, limit);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<UserDto> users = service.getAll(ids, pageable);
        log.info("Request GET /admin/users processed: {}", users);
        return users;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Request received: DELETE /admin/users/userId={}", userId);
        service.delete(userId);
        log.info("Request received: DELETE /admin/users/userId processed");
    }
}
