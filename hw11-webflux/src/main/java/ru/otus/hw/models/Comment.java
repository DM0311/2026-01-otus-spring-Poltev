package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "comments")
@Getter
public class Comment {

    @Id
    private final long id;

    @NotNull
    private final String commentText;

    @NotNull
    private final Long bookId;

    @PersistenceCreator
    public Comment(Long id, @NotNull String commentText, @NotNull Long bookId) {
        this.id = id;
        this.commentText = commentText;
        this.bookId = bookId;
    }
}
