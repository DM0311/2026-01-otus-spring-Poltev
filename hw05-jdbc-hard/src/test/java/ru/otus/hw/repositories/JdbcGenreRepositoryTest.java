package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@Import(JdbcGenreRepository.class)
public class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnAllGenresList() {
        var actualGenres = repositoryJdbc.findAll();
        assertThat(actualGenres).containsExactly(
                new Genre(1L, "Genre_1"),
                new Genre(2L, "Genre_2"),
                new Genre(3L, "Genre_3"),
                new Genre(4L, "Genre_4"),
                new Genre(5L, "Genre_5"),
                new Genre(6L, "Genre_6")
        );
    }

    @DisplayName("должен загружать список всех жанров по списку id")
    @Test
    void shouldReturnAllGenresById() {
        List<Genre> genres = repositoryJdbc.findAllByIds(
                Set.of(1L, 3L, 5L));
        assertThat(genres)
                .containsExactly(
                        new Genre(1L, "Genre_1"),
                        new Genre(3L, "Genre_3"),
                        new Genre(5L, "Genre_5")
                );
    }
}
