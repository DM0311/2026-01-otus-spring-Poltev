package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long>, CommentRepositoryCustom {

}
