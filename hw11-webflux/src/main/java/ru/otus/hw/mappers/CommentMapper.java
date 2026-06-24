package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentMapper {
    public CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getCommentText());
    }
}
