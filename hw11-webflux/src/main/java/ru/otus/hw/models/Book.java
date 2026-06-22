package ru.otus.hw.models;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table(name = "books")
@Getter
public class Book {

    @Id
    private final long id;

    @NotNull
    private final String title;

    @NotNull
    private final Author author;

    private final List<Genre> genres;

    @PersistenceCreator

    public Book(long id, String title, Author author, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}
