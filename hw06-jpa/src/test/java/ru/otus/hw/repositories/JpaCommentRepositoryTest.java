package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Репозиторий для работы с комментариями")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Должен найти комментарий по ID")
    void shouldFindCommentById(){

        var comment = commentRepository.findById(1L);

        var expectedComment = testEntityManager.find(Comment.class, 1L);

        assertThat(comment).isPresent();
        var actualComment = comment.get();
        assertAll(
                () -> assertThat(actualComment.getCommentText()).isEqualTo(expectedComment.getCommentText()),
                () -> assertThat(actualComment.getId()).isEqualTo(expectedComment.getId()),
                () -> assertThat(actualComment.getBook().getId()).isEqualTo(expectedComment.getBook().getId()));
    }

    @Test
    @DisplayName("Должен найти все комментарии по ID книги")
    void shouldFindAllCommentsByBookId(){

        var comments = commentRepository.getAllCommentsByBookId(2);

        assertThat(comments).isNotEmpty()
                .allMatch(comment -> !comment.getCommentText().isEmpty())
                .allMatch(comment -> comment.getBook() != null);
    }

    @Test
    @DisplayName("Должен сохранить комментарий")
    void shouldSaveComment(){

        Book book = testEntityManager.find(Book.class, 1L);
        Comment comment = new Comment(0, "newComment", book);

        var savedComment = commentRepository.save(comment);
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(savedComment.getId()).isPositive();

        Comment commentFromDB = testEntityManager.find(Comment.class, comment.getId());

        assertThat(commentFromDB).isNotNull();
        assertAll(
                () -> assertThat(savedComment.getCommentText()).isEqualTo(commentFromDB.getCommentText()),
                () -> assertThat(savedComment.getBook().getId()).isEqualTo(commentFromDB.getBook().getId()));
    }

    @Test
    @DisplayName("Должен обновить комментарий")
    void shouldUpdateComment(){

        var comment = testEntityManager.find(Comment.class, 1L);
        String newCommentText = "new Text";
        comment.setCommentText(newCommentText);

        commentRepository.save(comment);
        testEntityManager.flush();
        testEntityManager.clear();

        Comment commentFromDB = testEntityManager.find(Comment.class, 1L);

        assertThat(commentFromDB.getCommentText()).isEqualTo(newCommentText);
    }

    @Test
    @DisplayName("Должен удалить комментарий")
    void shouldDeleteComment(){

        commentRepository.deleteById(1L);

        Comment commentFromDB = testEntityManager.find(Comment.class, 1L);

        assertThat(commentFromDB).isNull();
    }
}
