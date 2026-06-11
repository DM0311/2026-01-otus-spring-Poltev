package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
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
import ru.otus.hw.repositories.BookRepositoryCustom;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookRepositoryCustom bookRepositoryCustom;

    private final DatabaseClient databaseClient;

    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(long id) {
        return bookRepositoryCustom.findById(id)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Books with id=%d not found!".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepositoryCustom.findAll();
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(String title, long authorId, Set<Long> genresIds) {
        return save(0L, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id);
    }

    private Mono<BookDto> save(long id, String title, long authorId, Set<Long> genresIds) {

        boolean isNewBook = (id == 0L);

        Mono<Author> authorMono = authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found: " + authorId)));

        Mono<List<Genre>> genresListMono = genreRepository.findAllByIdIn(genresIds)
                .collectList()
                .filter(genres -> !genres.isEmpty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genres not found!")));

        return Mono.zip(authorMono, genresListMono)
                .flatMap(tuple -> {
                    Book book = new Book(id, title, authorId);
                    return bookRepository.save(book)
                            .flatMap(savedBook ->
                                    updateBookGenresLinkTable(savedBook.getId(), tuple.getT2(), isNewBook)
                                            .thenReturn(savedBook)
                            )
                            .map(savedBook -> bookMapper.mapToDto(savedBook, tuple.getT1(), tuple.getT2()));
                });
    }

    private Mono<Void> updateBookGenresLinkTable(long bookId, List<Genre> genres, boolean isNewBook) {
        if (isNewBook) {
            return addBooksGenres(bookId, genres);
        } else {
            return updateBooksGenres(bookId, genres);
        }
    }

    private Mono<Void> addBooksGenres(long bookId, List<Genre> genres) {
        return Flux.fromIterable(genres)
                .flatMap(genre ->
                        databaseClient.sql("INSERT INTO books_genres (book_id, genre_id) VALUES ($1, $2)")
                                .bind("$1", bookId)
                                .bind("$2", genre.getId())
                                .then()
                )
                .then();
    }

    private Mono<Void> updateBooksGenres(long bookId, List<Genre> genres) {
        return databaseClient.sql("DELETE FROM books_genres WHERE book_id = $1")
                .bind("$1", bookId)
                .then()
                .thenMany(Flux.fromIterable(genres))
                .flatMap(genre ->
                        databaseClient.sql("INSERT INTO books_genres (book_id, genre_id) VALUES ($1, $2)")
                                .bind("$1", bookId)
                                .bind("$2", genre.getId())
                                .then()
                )
                .then();
    }
}

