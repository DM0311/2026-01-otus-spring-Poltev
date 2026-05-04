package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager entityManager;

    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> getAllCommentsByBookId(long bookId) {
        return entityManager.createQuery("select c from Comment c where c.book.id = :bookId", Comment.class)
                .setParameter("bookId",bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0L) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public Comment update(long id, String text) {
        Comment comment = entityManager.find(Comment.class, id);
        if (comment == null) {
            throw new EntityNotFoundException("Comment with not found. Id=%d".formatted(id));
        }
        comment.setCommentText(text);
        return comment;
    }

    @Override
    public void deleteById(long id) {
        Comment comment = entityManager.find(Comment.class, id);
        if (comment != null) {
            entityManager.remove(comment);
        }
    }
}
