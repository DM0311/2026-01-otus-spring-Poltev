package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private static final String COMMENTS_VIEW_FORM = "comments/book_comments_overview";

    private static final String COMMENTS_KEY = "comments";

    private static final String BOOK_KEY = "book";

    private final CommentService commentService;

    private final BookService bookService;

    @GetMapping("/comments")
    public String getComments(@RequestParam("id") long id, Model model) {
        Optional<BookDto> bookDto = bookService.findById(id);
        if (bookDto.isEmpty()) {
            throw new EntityNotFoundException("Entity with id %d not found".formatted(id));
        }
        List<CommentDto> comments = commentService.findAllByBookId(id);
        model.addAttribute(COMMENTS_KEY, comments);
        model.addAttribute(BOOK_KEY, bookDto.get());
        return COMMENTS_VIEW_FORM;
    }

}
