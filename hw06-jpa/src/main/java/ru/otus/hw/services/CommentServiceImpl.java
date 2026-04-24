package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(commentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findAllByBookId(long bookId) {
        return commentMapper.mapToDtoList(commentRepository.getAllCommentsByBookId(bookId));
    }

    @Transactional
    @Override
    public CommentDto save(String text, long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        Comment comment = new Comment();
        comment.setCommentText(text);
        comment.setBook(book);
        return commentMapper.mapToDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(long id, String text) {
        return commentMapper.mapToDto(commentRepository.update(id, text));

    }

    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
