package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/book")
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/book/{id}")
    public Mono<BookDto> getBookById(@PathVariable("id") long id) {
        return bookService.findById(id);
    }


    @PutMapping("/api/book/{id}")
    public Mono<BookDto> editBook(@PathVariable("id") long id,
                                  @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.update(id,
                bookUpdateDto.title(),
                bookUpdateDto.authorId(),
                bookUpdateDto.genreIds());
    }

    @PostMapping("/api/book")
    public Mono<BookDto> createBook(@Valid @RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.insert(bookUpdateDto.title(), bookUpdateDto.authorId(), bookUpdateDto.genreIds());
    }

    @DeleteMapping(value = "/api/book/{id}")
    public Mono<Void> deleteBook(@PathVariable("id") long id) {
        return bookService.deleteById(id);
    }
}
