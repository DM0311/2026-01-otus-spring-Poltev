package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    @DisplayName("Должен вернуть все комментарии к книге")
    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<CommentDto> comments = List.of(
                new CommentDto(1L,"Comments_1"),
                new CommentDto(2L,"Comments_2"));

        when(commentService.findAllByBookId(1L)).thenReturn(Flux.fromIterable(comments));

        webTestClient.get()
                .uri("/api/book/1/comment")
                .exchange()
                //then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("2");
    }
}
