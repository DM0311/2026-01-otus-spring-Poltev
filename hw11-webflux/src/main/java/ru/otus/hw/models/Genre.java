package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "genres")
@Getter
public class Genre {

    @Id
    private final long id;

    @NotNull
    private final String name;

    @PersistenceCreator
    public Genre(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
}
