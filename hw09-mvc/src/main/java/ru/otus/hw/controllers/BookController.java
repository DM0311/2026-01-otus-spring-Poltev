package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class BookController {

    private static final String BOOKS_VIEW = "books/books_overview";

    private static final String EDIT_BOOK_VIEW = "books/edit_book";

    private static final String REDIRECT_TO_ROOT = "redirect:/";

    private static final String BOOKS_KEY = "books";

    private static final String BOOK_KEY = "book";

    private static final String AUTHORS_KEY = "authors";

    private static final String GENRES_KEY = "genres";

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String getAllBooks(Model model) {
        model.addAttribute(BOOKS_KEY, bookService.findAll());
        return BOOKS_VIEW;
    }

    @GetMapping("/edit_book")
    public String editBook(@RequestParam(value = "id", required = false) Long id, Model model) {

        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute(AUTHORS_KEY, authors);
        model.addAttribute(GENRES_KEY, genres);

        if (id == null) {
            AuthorDto emptyAuthor = new AuthorDto(null, "");  // предположим, AuthorDto record или класс
            List<GenreDto> emptyGenres = List.of();
            BookDto dummy = new BookDto(null, "", emptyAuthor, emptyGenres);
            model.addAttribute(BOOK_KEY, dummy);
        } else {
            Optional<BookDto> bookDto = bookService.findById(id);
            if (bookDto.isEmpty()) {
                throw new EntityNotFoundException("Entity with id %d not found".formatted(id));
            }
            model.addAttribute(BOOK_KEY, bookDto.get());
        }

        return EDIT_BOOK_VIEW;
    }

    @PostMapping("/update")
    public String updateBook(@ModelAttribute("book") BookDto bookDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "genreIds", required = false) Set<Long> genreIds,
                             Model model) {
        if (genreIds == null || genreIds.isEmpty()) {
            String errorText = "Genre must not be empty";
            bindingResult.rejectValue("genres", "", errorText);
        }
        if (bindingResult.hasErrors()) {
            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();

            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);
            return EDIT_BOOK_VIEW;
        }

        bookService.update(bookDto.id(), bookDto.title(), bookDto.author().id(), genreIds);
        return REDIRECT_TO_ROOT;
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") BookDto bookDto,
                           BindingResult bindingResult,
                           @RequestParam(value = "genreIds", required = false) Set<Long> genreIds,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(AUTHORS_KEY, authorService.findAll());
            model.addAttribute(GENRES_KEY, genreService.findAll());
            return EDIT_BOOK_VIEW;
        }
        bookService.insert(bookDto.title(), bookDto.author().id(), genreIds);
        return REDIRECT_TO_ROOT;
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return REDIRECT_TO_ROOT;
    }
}
