package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto mapToDto(Book book, Author author, List<Genre> genres) {
        if (book == null) {
            return null;
        }
        List<GenreDto>genreDtos = genres.stream()
                .map(genreMapper::mapToDto)
                .toList();

        return new BookDto(book.getId(),
                book.getTitle(),
                authorMapper.mapToDto(author),
                genreDtos);
    }
}
