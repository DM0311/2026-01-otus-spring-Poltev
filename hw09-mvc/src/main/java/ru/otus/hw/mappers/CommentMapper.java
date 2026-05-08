package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.List;

@Component
public class CommentMapper {
    public CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getCommentText());
    }

    public List<CommentDto> mapToDtoList(List<Comment> comments) {
        return comments.stream().map(this::mapToDto).toList();
    }
}
