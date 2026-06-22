package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(long id) {
        return bookRepository.findBookEntityById(id).map(bookMapper::mapToDto)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Books with id=%d not found!".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepository.findAllBookEntities().map(bookMapper::mapToDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(String title, long authorId, Set<Long> genresIds) {
        return save(0L, title, authorId, genresIds).map(bookMapper::mapToDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds).map(bookMapper::mapToDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id);
    }

    private Mono<Book> save(Long id, String title, long authorId, Set<Long> genresIds) {

        Mono<Author> authorMono = getAuthorById(authorId);

        Mono<List<Genre>> genresListMono = getGenresByIds(genresIds);

        return Mono.zip(authorMono, genresListMono)
                .map(tuple -> new Book(id, title, tuple.getT1(), tuple.getT2()))
                .flatMap(book -> id == 0L
                        ? bookRepository.saveBookEntity(book)
                        : bookRepository.updateBookEntity(book));
    }

    private Mono<List<Genre>> getGenresByIds(Set<Long> genresIds) {
        return genreRepository.findAllByIdIn(genresIds)
                .collectList()
                .filter(genres -> !genres.isEmpty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genres not found!")));
    }

    private Mono<Author> getAuthorById(long authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found: " + authorId)));
    }
}

