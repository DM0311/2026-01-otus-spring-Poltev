package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto mapToDto(Book book) {

        if (book == null) {
            return null;
        }
        return new BookDto(book.getId(),
                book.getTitle(),
                authorMapper.mapToDto(book.getAuthor()),
                genreMapper.mapToDtoList(book.getGenres()));
    }
}
