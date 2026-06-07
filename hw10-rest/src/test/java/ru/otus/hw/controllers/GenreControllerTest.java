package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private GenreService genreService;

    @DisplayName("Должен вернуть все жанры")
    @Test
    void shouldReturnAllGenres() throws Exception {
        List<GenreDto> genres = List.of(new GenreDto(1L, "Genre_1"));
        given(genreService.findAll()).willReturn(genres);
        mvc.perform(get("/api/genre"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(genres)))
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}
