package ru.otus.hw.repositories;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private static final String FIND_ALL = """
            select
                            b.id as book_id,
                            b.title as book_title,
                            a.id as author_id,
                            a.full_name as author_full_name,
                            g.id as genre_id,
                            g.name as genre_name
                        from books b
                        join authors a on a.id = b.author_id
                        left join books_genres bg on bg.book_id = b.id
                        left join genres g on g.id = bg.genre_id
                        order by b.id, g.id
            """;

    private static final String FIND_BY_ID = """
            select
                            b.id as book_id,
                            b.title as book_title,
                            a.id as author_id,
                            a.full_name as author_full_name,
                            g.id as genre_id,
                            g.name as genre_name
                        from books b
                        join authors a on a.id = b.author_id
                        left join books_genres bg on bg.book_id = b.id
                        left join genres g on g.id = bg.genre_id
                        where b.id = :bookId
                        order by g.id
            """;

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Book> findBookEntityById(long id) {
        return template.getDatabaseClient()
                .sql(FIND_BY_ID)
                .bind("bookId", id)
                .map(this::mapToBookSelectRow)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(this::createBook);
    }

    @Override
    public Flux<Book> findAllBookEntities() {
        return template.getDatabaseClient()
                .sql(FIND_ALL)
                .map(this::mapToBookSelectRow)
                .all()
                .collectMultimap(BookSelectRow::bookId)
                .flatMapMany(grouped -> Flux.fromIterable(grouped.values()))
                .map(this::createBook);
    }

    @Override
    public Mono<Book> saveBookEntity(Book book) {
        return template.getDatabaseClient()
                .sql("""
                        insert into books(title, author_id)
                        values (:title, :authorId)
                        """)
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .flatMap(savedBookId -> addGenresToBook(savedBookId, book.getGenres())
                        .then(findBookEntityById(savedBookId)));
    }

    @Override
    public Mono<Book> updateBookEntity(Book book) {
        return template.getDatabaseClient()
                .sql("""
                        update books
                        set title = :title,
                            author_id = :authorId
                        where id = :bookId
                        """)
                .bind("bookId", book.getId())
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .then()
                .then(removeGenresFromBook(book.getId()))
                .then(addGenresToBook(book.getId(), book.getGenres()))
                .then(findBookEntityById(book.getId()));
    }

    private record BookSelectRow(
            Long bookId,
            String bookTitle,
            Long authorId,
            String authorFullName,
            Long genreId,
            String genreName
    ) {
    }

    private BookSelectRow mapToBookSelectRow(Row row, RowMetadata metadata) {
        return new BookSelectRow(
                row.get("book_id", Long.class),
                row.get("book_title", String.class),
                row.get("author_id", Long.class),
                row.get("author_full_name", String.class),
                row.get("genre_id", Long.class),
                row.get("genre_name", String.class)
        );
    }

    private Book createBook(Collection<BookSelectRow> rows) {
        BookSelectRow firstRow = rows.iterator().next();

        Author author = new Author(
                firstRow.authorFullName(),
                firstRow.authorId()
        );

        List<Genre> genres = rows.stream()
                .filter(row -> row.genreId() != null)
                .map(row -> new Genre(
                        row.genreId(),
                        row.genreName()
                ))
                .toList();

        return new Book(
                firstRow.bookId(),
                firstRow.bookTitle(),
                author,
                genres
        );
    }

    private Mono<Void> removeGenresFromBook(Long bookId) {
        return template.getDatabaseClient()
                .sql("""
                        delete from books_genres
                        where book_id = :bookId
                        """)
                .bind("bookId", bookId)
                .then();
    }

    private Mono<Void> addGenresToBook(Long bookId, List<Genre> genres) {
        return Flux.fromIterable(genres)
                .flatMap(genre -> addGenreToBook(bookId, genre))
                .then();
    }

    private Mono<Void> addGenreToBook(Long bookId, Genre genre) {
        return template.getDatabaseClient()
                .sql("""
                        insert into books_genres(book_id, genre_id)
                        values (:bookId, :genreId)
                        """)
                .bind("bookId", bookId)
                .bind("genreId", genre.getId())
                .then();
    }
}
