package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Репозиторий для работы с авторами")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    private static final int AUTHORS_INITIAL_COUNT = 3;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Должен найти всех авторов")
    void shouldFindAllAuthors() {

    }

    @Test
    @DisplayName("Должен найти автора по ID")
    void shouldFindAuthorById() {

        Optional<Author> author = authorRepository.findById(1L);

        Author expectedAuthor = testEntityManager.find(Author.class, 1L);

        assertThat(author).isPresent();
        assertThat(author.get()).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }
}
