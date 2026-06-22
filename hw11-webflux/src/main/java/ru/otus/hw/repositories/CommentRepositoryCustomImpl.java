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
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private static final String FIND_ALL_BY_BOOK_ID = """
            select
                            c.id as comment_id,
                            c.comment_text as comment_text,
                            b.id as book_id,
                            b.title as book_title,
                            a.id as author_id,
                            a.full_name as author_full_name,
                            g.id as genre_id,
                            g.name as genre_name
                        from comments c
                        join books b on b.id = c.book_id
                        join authors a on a.id = b.author_id
                        left join books_genres bg on bg.book_id = b.id
                        left join genres g on g.id = bg.genre_id
                        where b.id = :bookId
                        order by b.id, g.id
            """;

    private static final String FIND_BY_ID = """
            select
                            c.id as comment_id,
                            c.comment_text as comment_text,
                            b.id as book_id,
                            b.title as book_title,
                            a.id as author_id,
                            a.full_name as author_full_name,
                            g.id as genre_id,
                            g.name as genre_name
                        from comments c
                        join books b on b.id = c.book_id
                        join authors a on a.id = b.author_id
                        left join books_genres bg on bg.book_id = b.id
                        left join genres g on g.id = bg.genre_id
                        where c.id = :commentId
                        order by b.id, g.id
            """;

    private final R2dbcEntityTemplate template;


    @Override
    public Flux<Comment> findCommentEntityByBookId(long id) {
        return template.getDatabaseClient()
                .sql(FIND_ALL_BY_BOOK_ID)
                .bind("bookId", id)
                .map(this::mapToCommentSelectRow)
                .all()
                .collectMultimap(CommentSelectRow::commentId)
                .flatMapMany(grouped -> Flux.fromIterable(grouped.values()))
                .map(this::createComment);

    }

    @Override
    public Mono<Comment> findCommentEntityById(long id) {
        return template.getDatabaseClient()
                .sql(FIND_BY_ID)
                .bind("commentId", id)
                .map(this::mapToCommentSelectRow)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(this::createComment);
    }

    @Override
    public Mono<Comment> saveCommentEntity(Comment comment) {
        return template.getDatabaseClient()
                .sql("""
                        insert into comments(comment_text, book_id)
                        values (:commentText, :bookId)
                        """)
                .bind("commentText", comment.getCommentText())
                .bind("bookId", comment.getBook().getId())
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .flatMap(this::findCommentEntityById);
    }

    @Override
    public Mono<Comment> updateCommentEntity(Comment comment) {
        return template.getDatabaseClient()
                .sql("""
                        update comments
                        set comment_text = :comment_text,
                            book_id = :book_id
                        where id = :commentId
                        """)
                .bind("comment_text", comment.getCommentText())
                .bind("book_id", comment.getBook().getId())
                .bind("commentId", comment.getId())
                .then()
                .then(findCommentEntityById(comment.getId()));
    }

    private record CommentSelectRow(
            Long commentId,
            String commentText,
            Long bookId,
            String bookTitle,
            Long authorId,
            String authorFullName,
            Long genreId,
            String genreName
    ) {
    }

    private CommentSelectRow mapToCommentSelectRow(Row row, RowMetadata metadata) {
        return new CommentSelectRow(
                row.get("comment_id", Long.class),
                row.get("comment_text", String.class),
                row.get("book_id", Long.class),
                row.get("book_title", String.class),
                row.get("author_id", Long.class),
                row.get("author_full_name", String.class),
                row.get("genre_id", Long.class),
                row.get("genre_name", String.class)
        );
    }

    private Comment createComment(Collection<CommentSelectRow> rows) {
        CommentSelectRow firstRow = rows.iterator().next();

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

        Book book = new Book(
                firstRow.bookId(),
                firstRow.bookTitle(),
                author,
                genres
        );

        return new Comment(firstRow.commentId(), firstRow.commentText(), book);
    }
}
