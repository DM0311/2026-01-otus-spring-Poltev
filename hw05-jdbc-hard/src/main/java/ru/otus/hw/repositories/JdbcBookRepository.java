package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {

        Book book = jdbc.query("""
                select b.id as book_id,
                       b.title as book_title,
                       a.id as author_id,
                       a.full_name as author_name,
                       g.id as genre_id,
                       g.name as genre_name 
                from books b
                join authors a on a.id = b.author_id
                left join books_genres bg on bg.book_id = b.id
                left join genres g on g.id = bg.genre_id
                where b.id = :id  
                """, new MapSqlParameterSource("id", id), new BookResultSetExtractor());

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("delete from books where id = :id",
                new MapSqlParameterSource("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbc.query("""
                SELECT b.id AS book_id,
                       b.title AS book_title,
                       a.id AS author_id,
                       a.full_name AS author_name
                FROM books b
                JOIN authors a ON a.id = b.author_id
                """, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("select book_id, genre_id from books_genres",
                (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"),
                        rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {

        Map<Long, Book> booksById = new HashMap<>();
        for (Book book : booksWithoutGenres) {
            booksById.put(book.getId(), book);
        }

        Map<Long, Genre> genresById = new HashMap<>();
        for (Genre genre : genres) {
            genresById.put(genre.getId(), genre);
        }

        for (BookGenreRelation relation : relations) {
            Book book = booksById.get(relation.bookId);
            Genre genre = genresById.get(relation.genreId);
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var queryParams = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());
        jdbc.update("insert into books (title, author_id) values (:title, :author_id)", queryParams, keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var params = new MapSqlParameterSource()
                .addValue("title",book.getTitle())
                .addValue("authorId",book.getAuthor().getId())
                .addValue("id",book.getId());
        int updatedCount = jdbc.update("update books set title = :title, author_id = :authorId where id = :id", params);
        if(updatedCount==0){
            throw new EntityNotFoundException("Book with id = %d not found".formatted(book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (book == null || book.getGenres().isEmpty()) {
            return;
        }
        var batchParams = book.getGenres().stream().map(genre -> new MapSqlParameterSource()
                        .addValue("bookId", book.getId())
                        .addValue("genreId", genre.getId()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate("insert into books_genres(book_id,genre_id) values(:bookId, :genreId)", batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        jdbc.update("delete from books_genres where book_id = :bookId",
                new MapSqlParameterSource("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            String bookTitle = rs.getString("book_title");
            Author author = new Author(rs.getLong("author_id"), rs.getString("author_name"));
            return new Book(bookId, bookTitle, author, new ArrayList<>());
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                if (book == null) {
                    long bookId = rs.getLong("book_id");
                    String bookTitle = rs.getString("book_title");
                    long authorId = rs.getLong("author_id");
                    String authorName = rs.getString("author_name");
                    book = new Book(bookId,bookTitle,new Author(authorId,authorName),genres);
                }
                long genreId = rs.getLong("genre_id");
                if (!rs.wasNull()) {
                    genres.add(new Genre(genreId, rs.getString("genre_name")));
                }
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
