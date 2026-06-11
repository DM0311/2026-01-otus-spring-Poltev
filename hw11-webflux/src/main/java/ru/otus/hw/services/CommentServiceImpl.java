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
        return commentRepository.findById(id).map(commentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<CommentDto> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId).map(commentMapper::mapToDto);
    }

    @Transactional
    @Override
    public Mono<CommentDto> save(String text, long bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(() ->
                        new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> {
                    Comment comment = new Comment(0L, text, bookId);
                    return commentRepository.save(comment);
                })
                .map(commentMapper::mapToDto);
    }

    @Transactional
    @Override
    public Mono<CommentDto> update(long id, String text, long bookId) {
        return commentRepository.save(new Comment(id, text, bookId))
                .map(commentMapper::mapToDto);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        return commentRepository.deleteById(id);
    }
}
