package ru.otus.hw.models;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "books")
@Getter
public class Book {

    @Id
    private final long id;

    @NotNull
    private final String title;

    @NotNull
    private final Long authorId;

    @PersistenceCreator
    public Book(Long id, @NotNull String title, @NotNull Long authorId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
    }
}
