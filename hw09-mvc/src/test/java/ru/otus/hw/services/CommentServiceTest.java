package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.CommentDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("Должен возвращать комментарий по ID")
    void shouldReturnCommentById() {
        CommentDto comment = commentService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            assertThat(comment.id()).isPositive();
            assertThat(comment.text()).isNotBlank();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен возвращать комментарии по ID книги")
    void shouldReturnCommentsByBookId() {
        List<CommentDto> comments = commentService.findAllByBookId(1L);
        assertThat(comments).isNotEmpty();

        assertThatCode(() -> comments.forEach(comment -> {
            assertThat(comment.id()).isPositive();
            assertThat(comment.text()).isNotBlank();
        })).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен сохранять новый комментарий по ID книги")
    void shouldInsertNewComment() {
        commentService.save("Новый комментарий 1", 1L);
        List<CommentDto> comments = commentService.findAllByBookId(1L);
        assertThat(comments)
                .extracting(CommentDto::text)
                .contains("Новый комментарий 1");
    }

    @Test
    @DisplayName("Должен обновлять комментарий по ID")
    void shouldUpdateCommentById() {
        List<CommentDto> oldComments = commentService.findAllByBookId(1L);
        var firstOldComment = oldComments.get(0);
        commentService.update(firstOldComment.id(), "Обновленный комментарий");
        CommentDto updatedComment = commentService.findById(firstOldComment.id()).orElseThrow();
        assertThat(updatedComment)
                .extracting(CommentDto::text)
                .isEqualTo("Обновленный комментарий");
    }
}
