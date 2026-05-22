package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

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
        BookUpdateDto updateDto = new BookUpdateDto(null, "Test Book", 1L, Set.of(1L, 2L));

        mvc.perform(post("/save")
                        .flashAttr("updateDto", updateDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(
                updateDto.title(),
                updateDto.authorId(),
                updateDto.genreIds()
        );
    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() throws Exception {

        BookUpdateDto updateDto = new BookUpdateDto(1L, "Updated Book Title", 2L, Set.of(2L, 3L));

        mvc.perform(post("/save")
                        .flashAttr("updateDto", updateDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).update(
                1L,
                updateDto.title(),
                updateDto.authorId(),
                updateDto.genreIds()
        );
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
