package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Mono<CommentDto> findById(long id) {
        return commentRepository.findCommentEntityById(id).map(commentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<CommentDto> findAllByBookId(long bookId) {
        return commentRepository.findCommentEntityByBookId(bookId).map(commentMapper::mapToDto);
    }

    @Transactional
    @Override
    public Mono<CommentDto> save(String text, long bookId) {
        return update(0L, text, bookId);
    }

    @Transactional
    @Override
    public Mono<CommentDto> update(long id, String text, long bookId) {
        return bookRepository.findBookEntityById(bookId)
                .switchIfEmpty(Mono.error(() ->
                        new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> {
                    Comment comment = new Comment(id, text, book);
                    if (id == 0L) {
                        return commentRepository.saveCommentEntity(comment);
                    } else {
                        return commentRepository.findCommentEntityById(id)
                                .switchIfEmpty(Mono.error(() ->
                                        new EntityNotFoundException("Comment with id %d not found".formatted(id))))
                                .flatMap(existing -> {
                                    return commentRepository.updateCommentEntity(comment);
                                });
                    }
                })
                .map(commentMapper::mapToDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        return commentRepository.deleteById(id);
    }
}
