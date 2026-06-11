package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "authors")
@Getter
public class Author {

    @Id
    private final long id;

    @NotNull
    private final String fullName;

    @PersistenceCreator
    public Author(@NotNull String fullName, long id) {
        this.fullName = fullName;
        this.id = id;
    }
}
