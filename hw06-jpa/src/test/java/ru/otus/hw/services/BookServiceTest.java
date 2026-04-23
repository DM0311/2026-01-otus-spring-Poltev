package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Должен возвращать книгe с автором и жанрами")
    void shouldReturnBookById() {
        BookDto book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            assertThat(book.id()).isPositive();
            assertThat(book.title()).isNotBlank();

            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isPositive();
            assertThat(book.author().fullName()).isNotBlank();

            assertThat(book.genres()).isNotNull();
            assertThat(book.genres()).isNotEmpty();
            book.genres().forEach(genre -> {
                assertThat(genre).isNotNull();
                assertThat(genre.id()).isPositive();
                assertThat(genre.name()).isNotBlank();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен возвращать список книг с доступными автором и жанрами")
    void shouldReturnAllBooks() {

        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty();

        assertThatCode(() -> books.forEach(book -> {
            assertThat(book.id()).isPositive();
            assertThat(book.title()).isNotBlank();

            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isPositive();
            assertThat(book.author().fullName()).isNotBlank();

            assertThat(book.genres()).isNotNull();
            book.genres().forEach(genre -> {
                assertThat(genre).isNotNull();
                assertThat(genre.id()).isPositive();
                assertThat(genre.name()).isNotBlank();
            });
        })).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен сохранять книгу")
    void shouldSaveBook() {

        BookDto book = bookService.insert("new Test Book", 1, Set.of(1L, 2L));
        assertThatCode(() -> {
            assertThat(book.id()).isPositive();
            assertThat(book.title()).isNotBlank();

            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isPositive();
            assertThat(book.author().fullName()).isNotBlank();

            assertThat(book.genres()).isNotNull();
            book.genres().forEach(genre -> {
                assertThat(genre).isNotNull();
                assertThat(genre.id()).isPositive();
                assertThat(genre.name()).isNotBlank();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен обновлять книгу по ID")
    void shouldUpdateBook() {
        BookDto oldBook = bookService.findById(1L).orElseThrow();
        BookDto updated = bookService.update(oldBook.id(),
                "New test Title",
                oldBook.author().id(),
                Set.of(1L, 2L));
        assertThat(oldBook.id()).isEqualTo(updated.id());
        assertThat(oldBook.title()).isNotEqualTo(updated.title());
    }
}
