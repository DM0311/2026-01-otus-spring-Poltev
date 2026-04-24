package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    private static final int GENRES_INITIAL_COUNT = 6;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("Должен найти все жанры")
    void shouldFindAllGenres(){
        List<Genre> genres = genreRepository.findAll();
        assertThat(genres).hasSize(GENRES_INITIAL_COUNT);
    }

    @Test
    @DisplayName("Должен найти жанры по списку ID")
    void shouldFindGenreByIds(){

        List<Genre> genres = genreRepository.findAllByIds(Set.of(1L, 2L));

        Genre genre1 = testEntityManager.find(Genre.class, 1L);
        Genre genre2 = testEntityManager.find(Genre.class, 2L);
        List<Genre> expectedGenres = List.of(genre1, genre2);

        assertThat(genres).isNotEmpty();
        assertThat(genres).usingRecursiveComparison().isEqualTo(expectedGenres);
    }
}
