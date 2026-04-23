package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by Id", key = "cbid")
    public String findById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find all comments by book ID", key = "acbbid")
    public String findAllCommentsByBookId(long bookId) {
        return commentService.findAllByBookId(bookId)
                .stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));

    }

    @ShellMethod(value = "Save comments", key = "sc")
    public String saveComment(String commentText, long bookId) {
        CommentDto saved = commentService.save(commentText, bookId);
        return commentConverter.commentToString(saved);
    }

    @ShellMethod(value = "Update comment", key = "uc")
    public String updateComment(String newCommentText, long id) {
        CommentDto updated = commentService.update(id, newCommentText);
        return commentConverter.commentToString(updated);
    }

    @ShellMethod(value = "Delete comments", key = "dc")
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
