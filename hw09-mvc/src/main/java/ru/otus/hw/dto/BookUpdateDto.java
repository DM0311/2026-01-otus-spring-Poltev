package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BookUpdateDto(

        Long id,

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,

        @NotNull(message = "Author is required")
        Long authorId,

        @NotEmpty(message = "Genre must not be empty")
        Set<Long> genreIds
) {
}
