package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @DisplayName("Должен вернуть все книги")
    @Test
    void shouldReturnAllAuthors() throws Exception {

        List<AuthorDto> authors = List.of(
                new AuthorDto(1L, "Author1"),
                new AuthorDto(2L, "Author2"));

        when(authorService.findAll()).thenReturn(Flux.fromIterable(authors));

        webTestClient.get()
                .uri("/api/author")
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("2");
    }
}
