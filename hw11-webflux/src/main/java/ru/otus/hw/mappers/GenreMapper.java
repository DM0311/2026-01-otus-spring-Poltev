package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreMapper {

    public GenreDto mapToDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
