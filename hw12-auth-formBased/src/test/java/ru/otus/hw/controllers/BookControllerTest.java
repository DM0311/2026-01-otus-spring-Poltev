package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookService bookService;

    @DisplayName("Должен вернуть все книги")
    @Test
    void shouldReturnAllBooks() throws Exception {
        List<BookDto> books = List.of(new BookDto(1L, "Title_1",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1"))));
        given(bookService.findAll()).willReturn(books);
        mvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)))
                .andExpect(jsonPath("$[0].author.id").value("1"));

    }

    @DisplayName("Должен вернуть книгу по id")
    @Test
    void shouldReturnBookById() throws Exception {
        BookDto book = new BookDto(1L, "Title_1",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1")));
        given(bookService.findById(1L)).willReturn(book);
        mvc.perform(get("/api/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)))
                .andExpect(jsonPath("$.author.id").value("1"));

    }

    @DisplayName("Должен сохранить новую книгу")
    @Test
    void shouldAddNewBook() throws Exception {
        BookUpdateDto requestDto = new BookUpdateDto("Test Book", 1L, Set.of(1L, 2L));
        BookDto responseDto = new BookDto(1L, "Test Book",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1")));
        given(bookService.insert(requestDto.title(), requestDto.authorId(), requestDto.genreIds())).willReturn(responseDto);
        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.id").value(1L));

    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() throws Exception {

        BookUpdateDto request = new BookUpdateDto("Updated Book Title", 1L, Set.of(2L, 3L));
        BookDto response = new BookDto(1L, "Updated Book Title",
                new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(2L, "Genre_2"), new GenreDto(3L, "Genre_3")));

        given(bookService.update(1L, request.title(), request.authorId(), request.genreIds()))
                .willReturn(response);
        mvc.perform(put("/api/book/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book Title"));

        verify(bookService).update(
                1L,
                request.title(),
                request.authorId(),
                request.genreIds()
        );
    }


    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/book/1"))
                .andExpect(status().isOk());
        verify(bookService).deleteById(1L);
    }
}
