package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(BookController.class)
public class BookControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;


    @DisplayName("Должен вернуть страницу с книгами")
    @Test
    void shouldReturnBooksPage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/books_overview"))
                .andExpect(model().attributeExists("books"));
    }


    @DisplayName("Должен сохранить новую книгу")
    @Test
    void shouldAddNewBook() throws Exception {
        BookDto book = new BookDto(null, "Test Book", new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2")));
        Set<Long> genreIds = Set.of(1L, 2L);

        mvc.perform(post("/save")
                        .flashAttr("book", book)
                        .param("genreIds", "1", "2"))   // ← добавлено
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(
                book.title(),
                book.author().id(),
                genreIds
        );
    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() throws Exception {
        BookDto bookUpdate = new BookDto(1L, "Test Book", new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2")));
        Set<Long> genreIds = Set.of(1L, 2L);
        mvc.perform(post("/update")
                        .flashAttr("book", bookUpdate)
                        .param("genreIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService).update(1L, bookUpdate.title(), bookUpdate.author().id(), genreIds);
    }


    @DisplayName("Должен удалить книгу")
    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(post("/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService).deleteById(1L);
    }
}
