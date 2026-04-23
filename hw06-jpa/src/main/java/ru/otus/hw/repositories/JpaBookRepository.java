package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("book-with-author-genres-graph");
        Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", entityGraph);
        return Optional.ofNullable(entityManager.find(Book.class, id, hints));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("book-with-author-genres-graph");
        return entityManager.createQuery("select b from Book b", Book.class)
                .setHint("jakarta.persistence.fetchgraph", entityGraph)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = entityManager.find(Book.class, id);
        entityManager.remove(book);
    }
}
