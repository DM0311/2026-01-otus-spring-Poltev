package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthorService authorService;

    @DisplayName("Должен вернуть все книги")
    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(new AuthorDto(1L, "Author_1"));
        given(authorService.findAll()).willReturn(authors);
        mvc.perform(get("/api/author"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authors)))
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}
