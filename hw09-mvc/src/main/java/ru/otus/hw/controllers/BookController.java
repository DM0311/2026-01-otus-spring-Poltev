package ru.otus.hw.controllers;

import jakarta.validation.Valid;
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
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final String UPDATE_BOOK_KEY = "updateDto";

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
            BookUpdateDto updateDto = new BookUpdateDto(null, "", null, Set.of());
            model.addAttribute(UPDATE_BOOK_KEY, updateDto);
        } else {
            BookDto book = bookService.findById(id);
            BookUpdateDto updateDto = new BookUpdateDto(
                    book.id(),
                    book.title(),
                    book.author().id(),
                    book.genres().stream().map(GenreDto::id).collect(Collectors.toSet())
            );
            model.addAttribute(UPDATE_BOOK_KEY, updateDto);
        }
        return EDIT_BOOK_VIEW;
    }

    @PostMapping("/save")
    public String updateBook(@Valid @ModelAttribute("updateDto") BookUpdateDto updateDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();

            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);
            return EDIT_BOOK_VIEW;
        }

        if (updateDto.id() == null) {
            bookService.insert(updateDto.title(), updateDto.authorId(), updateDto.genreIds());
        } else {
            bookService.update(updateDto.id(), updateDto.title(), updateDto.authorId(), updateDto.genreIds());
        }
        return REDIRECT_TO_ROOT;
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return REDIRECT_TO_ROOT;
    }
}
