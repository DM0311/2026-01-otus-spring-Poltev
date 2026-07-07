package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto mapToDto(Book book) {
        return new BookDto(book.getId(),
                book.getTitle(),
                authorMapper.mapToDto(book.getAuthor()),
                genreMapper.mapToDtoList(book.getGenres()));
    }

    public List<BookDto> mapToListDto(List<Book> books) {
        return books.stream().map(this::mapToDto).toList();
    }
}
