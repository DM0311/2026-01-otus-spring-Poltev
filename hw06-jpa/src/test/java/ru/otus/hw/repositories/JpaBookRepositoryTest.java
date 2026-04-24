package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Репозиторий для работы с книгами")
@DataJpaTest
@Import(JpaBookRepository.class)
public class JpaBookRepositoryTest {

    private static final int BOOKS_INITIAL_COUNT = 3;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Должен найти книгу по ID")
    void shouldFindBookById(){

        var book = bookRepository.findById(1L);

        var expectedBook = testEntityManager.find(Book.class, 1L);

        assertThat(book).isPresent();
        Book actualBook = book.get();

        assertAll(
                () -> assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle()),
                () -> assertThat(actualBook.getAuthor()).usingRecursiveComparison().isEqualTo(expectedBook.getAuthor()),
                () -> assertThat(actualBook.getGenres()).usingRecursiveComparison().isEqualTo(expectedBook.getGenres()));
    }

    @Test
    @DisplayName("Должен найти все книги")
    void shouldFindAllBookd(){

        var books = bookRepository.findAll();

        assertThat(books).hasSize(BOOKS_INITIAL_COUNT);
        assertThat(books)
                .allMatch(b -> !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null)
                .allMatch(b -> b.getGenres() != null && !b.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Должен сохранить книгу")
    void shouldSaveBook(){

        Author author = testEntityManager.find(Author.class, 1);
        Genre genre1 = testEntityManager.find(Genre.class, 1L);
        Genre genre2 = testEntityManager.find(Genre.class, 2L);
        List<Genre> genres = List.of(genre1, genre2);
        Book book = new Book(0, "New Test Book", author, genres);

        var savedBook = bookRepository.save(book);
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(savedBook.getId()).isPositive();

        Book bookFromDB = testEntityManager.find(Book.class, savedBook.getId());

        assertThat(bookFromDB).isNotNull();

        List<Long> savedGenreIds = savedBook.getGenres().stream().map(Genre::getId).toList();
        List<Long> fromBDGenreIds = bookFromDB.getGenres().stream().map(Genre::getId).toList();

        assertAll(
                () -> assertThat(savedBook.getTitle()).isEqualTo(bookFromDB.getTitle()),
                () -> assertThat(savedBook.getAuthor().getId()).isEqualTo(bookFromDB.getAuthor().getId()),
                () -> assertThat(savedGenreIds).usingRecursiveComparison().isEqualTo(fromBDGenreIds));
    }

    @Test
    @DisplayName("Должен обновить книгу")
    void shouldUpdateBook(){

        var book = testEntityManager.find(Book.class, 1L);
        var oldAuthor = book.getAuthor();
        var oldTitle = book.getTitle();
        Author author = testEntityManager.find(Author.class, 2);
        Genre genre1 = testEntityManager.find(Genre.class, 5L);
        Genre genre2 = testEntityManager.find(Genre.class, 6L);
        List<Genre> genres = new ArrayList<>(List.of(genre1, genre2));

        String newTitle = "new Test Title";
        book.setTitle(newTitle);
        book.setGenres(genres);
        book.setAuthor(author);

        bookRepository.save(book);
        testEntityManager.flush();
        testEntityManager.clear();

        Book bookFromDB = testEntityManager.find(Book.class, 1L);

        List<Long> savedGenreIds = genres.stream().map(Genre::getId).toList();
        List<Long> fromBDGenreIds = bookFromDB.getGenres().stream().map(Genre::getId).toList();

        assertAll(
                () -> assertThat(book.getTitle()).isEqualTo(bookFromDB.getTitle()),
                () -> assertThat(book.getAuthor().getId()).isEqualTo(bookFromDB.getAuthor().getId()),
                () -> assertThat(bookFromDB.getAuthor().getId()).isNotEqualTo(oldAuthor.getId()),
                () -> assertThat(bookFromDB.getTitle()).isNotEqualTo(oldTitle),
                () -> assertThat(savedGenreIds).usingRecursiveComparison().isEqualTo(fromBDGenreIds));
    }

    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook(){

        bookRepository.deleteById(1L);

        Book bookFromDB = testEntityManager.find(Book.class, 1L);

        assertThat(bookFromDB).isNull();
    }
}
