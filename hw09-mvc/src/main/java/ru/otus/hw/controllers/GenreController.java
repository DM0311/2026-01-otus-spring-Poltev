package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class GenreController {

    private static final String GENRES_VIEW_FORM = "genres/genres_overview";

    private static final String GENRES_KEY = "genres";

    private final GenreService genreService;

    @GetMapping("/genres")
    public String getAuthors(Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute(GENRES_KEY, genres);
        return GENRES_VIEW_FORM;
    }

}
