package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AuthorController {

    private static final String AUTHORS_VIEW_FORM = "authors/authors_overview";

    private static final String AUTHORS_KEY = "authors";

    private final AuthorService authorService;

    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public String getAuthors(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute(AUTHORS_KEY, authors);
        return AUTHORS_VIEW_FORM;
    }

}
