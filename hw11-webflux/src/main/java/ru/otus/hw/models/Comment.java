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
    private final Book book;

    @PersistenceCreator

    public Comment(long id, String commentText, Book book) {
        this.id = id;
        this.commentText = commentText;
        this.book = book;
    }
}
