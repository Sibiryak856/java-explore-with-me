package ru.practicum.ewm.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAdminController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl service;

    @Autowired
    private MockMvc mvc;

    private UserCreateDto createDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createDto = UserCreateDto.builder()
                .name("name")
                .email("name@email.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@email.ru")
                .build();
    }

    @Test
    void save_WhenUserIsValid_thenStatusIsCreatedANdReturnUserDto() throws Exception {
        when(service.save(any(UserCreateDto.class)))
                .thenReturn(userDto);

        String result = mvc.perform(post("/admin/users")
                        .content(String.valueOf(mapper.writeValueAsString(createDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(userDto));
    }

    @Test
    void save_whenUserEmailIsNotValid_thenReturnBadRequest() throws Exception {
        createDto.setEmail("e.ru");
        mvc.perform(post("/admin/users")
                        .content(String.valueOf(mapper.writeValueAsString(createDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(UserCreateDto.class));
    }

    @Test
    void save_whenUserEmailIsNull_thenReturnBadRequest() throws Exception {
        createDto.setEmail(null);
        mvc.perform(post("/admin/users")
                        .content(String.valueOf(mapper.writeValueAsString(createDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(UserCreateDto.class));
    }

    @Test
    void save_whenUserNameIsBlank_thenReturnBadRequest() throws Exception {
        createDto.setName("");
        mvc.perform(post("/admin/users")
                        .content(String.valueOf(mapper.writeValueAsString(createDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(UserCreateDto.class));
    }

    @Test
    void getAll_whenArgsIsValid_thenReturnUsersList() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(service.getAll(null, PageRequest.of(5 / 10, 10)))
                .thenReturn(users);

        String result = mvc.perform(get("/admin/users")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(users));
    }

    @Test
    void getAll_whenParamFromIsFalse_thenReturnUsersList() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getAll(anyList(), any(Pageable.class));
    }

    @Test
    void getAll_whenParamSizeIsFalse_thenReturnUsersList() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getAll(anyList(), any(Pageable.class));
    }
}