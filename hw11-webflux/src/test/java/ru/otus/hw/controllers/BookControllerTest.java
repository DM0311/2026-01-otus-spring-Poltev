package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@WebFluxTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookService bookService;

    @DisplayName("Должен вернуть все книги")
    @Test
    void shouldReturnAllBooks() throws Exception {

        List<BookDto> books = List.of(
                new BookDto(1L, "Title1",
                        new AuthorDto(1L, "Author1"),
                        List.of(new GenreDto(1L, "Genre1"),
                                new GenreDto(2L, "Genre2"))),
                new BookDto(2L, "Title2",
                        new AuthorDto(2L, "Author2"),
                        List.of(new GenreDto(3L, "Genre3"),
                                new GenreDto(4L, "Genre4"))));

        when(bookService.findAll()).thenReturn(Flux.fromIterable(books));

        webTestClient.get()
                .uri("/api/book")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[1].id").isEqualTo("2");

    }

    @DisplayName("Должен вернуть книгу по id")
    @Test
    void shouldReturnBookById() throws Exception {

        BookDto book = new BookDto(1L, "Title1",
                new AuthorDto(1L, "Author1"),
                List.of(new GenreDto(1L, "Genre1"),
                        new GenreDto(2L, "Genre2")));

        when(bookService.findById(1)).thenReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/book/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");

    }

    @DisplayName("Должен сохранить новую книгу")
    @Test
    void shouldAddNewBook() throws Exception {

        BookUpdateDto requestDto = new BookUpdateDto("Test Book", 1L, Set.of(1L, 2L));
        BookDto responseDto = new BookDto(1L, "Test Book",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1")));

        when(bookService.insert(requestDto.title(), requestDto.authorId(), requestDto.genreIds()))
                .thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() throws Exception {

        BookUpdateDto requestDto = new BookUpdateDto("Updated Book Title", 1L, Set.of(2L, 3L));
        BookDto responseDto = new BookDto(1L, "Updated Book Title",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(2L, "Genre_2"), new GenreDto(3L, "Genre_3")));

        when(bookService.update(1L, requestDto.title(), requestDto.authorId(), requestDto.genreIds()))
                .thenReturn(Mono.just(responseDto));

        webTestClient.put()
                .uri("/api/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() {

        when(bookService.deleteById(anyLong())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/book/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
