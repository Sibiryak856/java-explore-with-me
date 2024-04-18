package ru.practicum.ewm.user.controller;

/*@WebMvcTest(controllers = UserAdminController.class)*/
class UserControllerTest {

    /*@Autowired
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
    void save_WhenUserIsValid_thenStatusIsCreatedAndReturnUserDto() throws Exception {
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
    void save_whenUserEmailIsNotValid_thenStatusIsBadRequest() throws Exception {
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
    void save_whenUserEmailIsNull_thenStatusIsBadRequest() throws Exception {
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
    void save_whenUserNameIsBlank_thenStatusIsBadRequest() throws Exception {
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
        when(service.getAll(null, new MyPageRequest(0, 10, Sort.unsorted())))
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
    void getAll_whenParamFromIsFalse_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getAll(anyList(), any(Pageable.class));
    }

    @Test
    void getAll_whenParamSizeIsFalse_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/admin/users")
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(service, never()).getAll(anyList(), any(Pageable.class));
    }*/
}