package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

public interface CommentRepositoryCustom {

    Flux<Comment> findCommentEntityByBookId(long id);

    Mono<Comment> findCommentEntityById(long id);

    Mono<Comment> saveCommentEntity(Comment comment);

    Mono<Comment> updateCommentEntity(Comment comment);

}

